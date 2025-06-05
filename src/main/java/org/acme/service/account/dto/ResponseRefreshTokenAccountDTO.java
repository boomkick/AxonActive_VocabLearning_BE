package org.acme.service.account.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseRefreshTokenAccountDTO {
    private String accessToken;
    private long expiresIn;
}
