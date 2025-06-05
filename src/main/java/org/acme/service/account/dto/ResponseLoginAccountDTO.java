package org.acme.service.account.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseLoginAccountDTO {
    private String accessToken;
    private long expiresIn;
    private long refreshExpiresIn;
    private String refreshToken;
}
