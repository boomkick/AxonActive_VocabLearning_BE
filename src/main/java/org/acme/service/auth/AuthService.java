package org.acme.service.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @Inject
    AccountDAO accountDAO;

    public Optional<String> authenticate(String username, String password) {
        Optional<Account> accountOpt = accountDAO.findByUsername(username);
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
                .claim("username", account.getUsername())
                .claim("roles", Set.of("USER")) // Adjust roles if needed
                .expiresAt(System.currentTimeMillis() / 1000 + 3600) // 1 hour expiry
                .sign();
    }

    @Transactional
    public void register(Account account) {
        account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountDAO.add(account);
    }
}