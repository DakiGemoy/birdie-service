package com.priv.birdie.model.request;

import jakarta.validation.constraints.NotBlank;

public record RegistAccountRequest(
        @NotBlank
        String email,
        @NotBlank
        String username,
        @NotBlank
        String password
){
}
