package org.acme.service.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.acme.service.account.dto.ResponseLoginAccountDTO;
import org.acme.shared.constant.Constant;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@ApplicationScoped
public class AuthService {

    @Inject
    AccountDAO accountDAO;

    @Inject
    RefreshTokenDAO refreshTokenDAO;

    public Optional<ResponseLoginAccountDTO> authenticate(String email, String password) {
        Optional<Account> accountOpt = accountDAO.findByEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(password, account.getPassword())) {
                long now = System.currentTimeMillis() / 1000;
                long expiresTime = now + Constant.EXPIRED_TIME;
                long expiresRefreshTime = now + (Constant.EXPIRED_TIME * 3);
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

    @Transactional
    public ResponseLoginAccountDTO refreshToken(JsonWebToken jwt) {
        // Decode jwt
        long now = System.currentTimeMillis() / 1000;
        // Check expire
        if (now < jwt.getExpirationTime()) {
            revokeRefreshToken(jwt.getClaim("token_id"));
            Optional<Account> accountOpt = accountDAO.findByEmail(jwt.getClaim("email"));
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                ResponseLoginAccountDTO responseLoginAccountDTO = new ResponseLoginAccountDTO();
                responseLoginAccountDTO.setAccessToken(generateToken(account, now));
                responseLoginAccountDTO.setExpiresIn(now + Constant.EXPIRED_TIME);
                responseLoginAccountDTO.setRefreshToken(generateRefreshToken(account, now));
                responseLoginAccountDTO.setRefreshExpiresIn(now + Constant.EXPIRED_TIME * 3);
                return responseLoginAccountDTO;
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
                .expiresAt(currentTime + Constant.EXPIRED_TIME) // 1 giờ
                .sign();
    }

    @Transactional
    public String generateRefreshToken(Account account, long currentTime) {
        long expiresAt = currentTime + (Constant.EXPIRED_TIME * 3);
        LocalDateTime expiresAtFormat = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(expiresAt),
                ZoneId.systemDefault()
        );

        RefreshToken refreshToken = new RefreshToken(account.getId(), expiresAtFormat, false);
        refreshTokenDAO.add(refreshToken);
        return Jwt.issuer("https://vocablearning.com/")
                .subject(account.getId())
                .upn(account.getEmail())
                .groups(Set.of("user")) // tất cả quyền của user
                .claim("email", account.getEmail())
                .claim("name", account.getName()) // nếu có name
                .claim("avatar", account.getAvatar())
                .claim("theme", account.getTheme())
                .claim("id", account.getId())
                .claim("token_id", refreshToken.getId())
                .issuedAt(currentTime)
                .expiresAt(expiresAt) // 1 giờ
                .sign();
    }

    private void revokeRefreshToken(String refreshTokenId) {
        if (refreshTokenId != null && !refreshTokenId.equals("")) {
            RefreshToken refreshToken = refreshTokenDAO.findById(refreshTokenId)
                    .orElseThrow(() -> new NotFoundException("Refresh token is invalid or mistake with access token."));
            refreshToken.setIsRevoked(true);
        }
    }


    @Transactional
    public void register(Account account) {
        account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
        accountDAO.add(account);
    }
}