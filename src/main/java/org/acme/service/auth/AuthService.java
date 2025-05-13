package org.acme.service.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    AccountDAO accountDAO;

    public Optional<String> authenticate(String email, String password) {
        Optional<Account> accountOpt = accountDAO.findByEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(password, account.getPassword())) {
                return Optional.of(generateToken(account));
            }
        }
        return Optional.empty();
    }

    private String generateToken(Account account) {
        return Jwt.issuer("https://yourdomain.com")
                .subject(account.getId())
                .claim("email", account.getEmail())
                .claim("name", "name")
                .claim("avatar", "avatar")
                .claim("theme", "theme")
                .claim("id", account.getId())
                .claim("role", account.getRole()) // Adjust roles if needed
                .expiresAt(System.currentTimeMillis() / 1000 + 3600) // 1 hour expiry
                .sign();
    }

    @Transactional
    public void register(Account account) {
        account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountDAO.add(account);
    }
}