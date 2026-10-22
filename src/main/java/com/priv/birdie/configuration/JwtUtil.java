package com.priv.birdie.configuration;

import com.priv.birdie.model.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties properties;

    public Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey((Keys.hmacShaKeyFor(Base64.getDecoder().decode(properties.getJwt().getSecret().getBytes()))))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username, Set<String> roles){
        return Jwts.builder()
                .setIssuer("birdie")
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+properties.getJwt().getExpired()))
                .claim("role",roles)
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(properties.getJwt().getSecret().getBytes())), SignatureAlgorithm.HS256)
                .compact();
    }

    public void getCurrentCredential(){}

}
