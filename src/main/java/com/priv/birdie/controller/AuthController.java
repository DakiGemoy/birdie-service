package com.priv.birdie.controller;

import com.priv.birdie.model.request.LoginRequest;
import com.priv.birdie.model.request.RefreshAccessTokenRequest;
import com.priv.birdie.model.request.RegisterAccountRequest;
import com.priv.birdie.model.response.BaseResponse;
import com.priv.birdie.model.response.LoginResponse;
import com.priv.birdie.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.priv.birdie.model.Constant.RespCode.SUCCESS;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return BaseResponse.<LoginResponse>builder()
                .code(SUCCESS)
                .message("Success login")
                .data(authenticationService.login(request))
                .build();
    }

    @PostMapping("/register/client")
    public BaseResponse<String> registerClient(@Valid @RequestBody RegisterAccountRequest request){
        authenticationService.registerAccountClient(request);
        return BaseResponse.<String>builder()
                .code(SUCCESS)
                .message("Registration successfull")
                .build();
    }

    @PostMapping("/refresh-token")
    public BaseResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshAccessTokenRequest request){
        return BaseResponse.<LoginResponse>builder()
                .code(SUCCESS)
                .message("Success refresh token")
                .data(authenticationService.extendAccessToken(request.refreshToken()))
                .build();
    }

    @GetMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest servletRequest){
        String refreshToken = servletRequest.getHeader("refresh");
        authenticationService.destroyRefreshToken(refreshToken);
        return BaseResponse.<Void>builder()
                .code("00")
                .message("Success logout")
                .build();
    }
}
