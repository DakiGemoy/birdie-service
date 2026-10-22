package com.priv.birdie.service;

import com.priv.birdie.configuration.JwtUtil;
import com.priv.birdie.configuration.ProcessException;
import com.priv.birdie.model.request.LoginRequest;
import com.priv.birdie.model.request.RegistAccountRequest;
import com.priv.birdie.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    public void createAccount(RegistAccountRequest request){
    }

    public String login(LoginRequest request){
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(),request.password())
            );

            Set<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> Objects.requireNonNull(authority).contains("ROLE"))
                    .collect(Collectors.toSet());
            return jwtUtil.generateToken(request.username(),roles);
        } catch (Exception e){
            throw new ProcessException(HttpStatus.UNAUTHORIZED,"Invalid username / password");
        }
    }
}
