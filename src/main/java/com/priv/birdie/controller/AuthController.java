package com.priv.birdie.controller;

import com.priv.birdie.model.request.LoginRequest;
import com.priv.birdie.model.response.BaseResponse;
import com.priv.birdie.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public BaseResponse<String> login(@Valid @RequestBody LoginRequest request){
        return BaseResponse.<String>builder()
                .code("00")
                .message("Success login")
                .data(authenticationService.login(request))
                .build();
    }
}
