package org.acme.service.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.acme.service.account.dto.ResponseLoginAccountDTO;
import org.acme.service.account.dto.ResponseRefreshTokenAccountDTO;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

@ApplicationScoped
public class AuthService {

    @Inject
    AccountDAO accountDAO;

    public Optional<ResponseLoginAccountDTO> authenticate(String email, String password) {
        Optional<Account> accountOpt = accountDAO.findByEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(password, account.getPassword())) {
                long now = System.currentTimeMillis() / 1000;
                long expiresTime = now + 3600;
                long expiresRefreshTime = now + (3600 * 3);
                ResponseLoginAccountDTO responseTokenAccountDTO = new ResponseLoginAccountDTO();
                responseTokenAccountDTO.setAccessToken(generateToken(account, now));
                responseTokenAccountDTO.setRefreshToken(generateRefreshToken(account, now));
                responseTokenAccountDTO.setExpiresIn(expiresTime);
                responseTokenAccountDTO.setRefreshExpiresIn(expiresRefreshTime);

                return Optional.of(responseTokenAccountDTO);
            }
        }
        return Optional.empty();
    }

    public ResponseRefreshTokenAccountDTO refreshToken(JsonWebToken jwt) {
        // Decode jwt
        long now = System.currentTimeMillis() / 1000;
        // Check expire
        if (now < jwt.getExpirationTime()) {
            long expiresTime = now + 3600;
            Optional<Account> accountOpt = accountDAO.findByEmail(jwt.getClaim("email"));
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                ResponseRefreshTokenAccountDTO responseRefreshTokenAccountDTO = new ResponseRefreshTokenAccountDTO();
                responseRefreshTokenAccountDTO.setAccessToken(generateToken(account, now));
                responseRefreshTokenAccountDTO.setExpiresIn(expiresTime);
                return responseRefreshTokenAccountDTO;
            }
            throw new RuntimeException("Account not found");
        }
        throw new RuntimeException("Refresh token is expired");
    }

    private String generateToken(Account account, long currentTime) {
        return Jwt.issuer("https://vocablearning.com/")
                .subject(account.getId())
                .upn(account.getEmail())
                .groups(Set.of("user")) // tất cả quyền của user
                .claim("email", account.getEmail())
                .claim("name", account.getName()) // nếu có name
                .claim("avatar", account.getAvatar())
                .claim("theme", account.getTheme())
                .claim("id", account.getId())
                .issuedAt(currentTime)
                .expiresAt(currentTime + 3600) // 1 giờ
                .sign();
    }

    private String generateRefreshToken(Account account, long currentTime) {
        return Jwt.issuer("https://vocablearning.com/")
                .subject(account.getId())
                .upn(account.getEmail())
                .groups(Set.of("user")) // tất cả quyền của user
                .claim("email", account.getEmail())
                .claim("name", account.getName()) // nếu có name
                .claim("avatar", account.getAvatar())
                .claim("theme", account.getTheme())
                .claim("id", account.getId())
                .issuedAt(currentTime)
                .expiresAt(currentTime + (3600 * 3)) // 1 giờ
                .sign();
    }


    @Transactional
    public void register(Account account) {
        account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountDAO.add(account);
    }
}