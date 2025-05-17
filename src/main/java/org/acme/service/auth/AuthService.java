package org.acme.service.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

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
        long now = System.currentTimeMillis() / 1000;
        return Jwt.issuer("https://vocablearning.com/")
                .subject(account.getId())
                .upn(account.getEmail())
                .groups(Set.of("user")) // tất cả quyền của user
                .claim("email", account.getEmail())
                .claim("name", account.getName()) // nếu có name
                .claim("avatar", account.getAvatar())
                .claim("theme", account.getTheme())
                .claim("id", account.getId())
                .issuedAt(now)
                .expiresAt(now + 3600) // 1 giờ
                .sign();
    }


    @Transactional
    public void register(Account account) {
        account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountDAO.add(account);
    }
}