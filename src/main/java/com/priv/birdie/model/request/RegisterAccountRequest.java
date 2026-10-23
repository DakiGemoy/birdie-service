package com.priv.birdie.model.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
        String displayName,
        @NotBlank
        String email,
        @NotBlank
        String username,
        @NotBlank
        String password,
        Boolean needTwoAuth
){
}
