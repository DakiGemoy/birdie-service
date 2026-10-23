package com.priv.birdie.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class LoginResponse {
    private String accessToken;
    private LocalDateTime expiredAt;
    private String refreshToken;
}
