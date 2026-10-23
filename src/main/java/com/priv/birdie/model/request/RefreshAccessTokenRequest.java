package com.priv.birdie.model.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshAccessTokenRequest(
        @NotBlank
        String refreshToken
) {
}
