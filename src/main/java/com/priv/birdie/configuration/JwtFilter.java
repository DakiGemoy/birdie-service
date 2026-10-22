package com.priv.birdie.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CustomAuthEntryPoint authEntryPoint;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.getClaimsFromToken(token);
                String username = claims.getSubject();
                List<String> role = (List<String>) claims.get("role");
                List<SimpleGrantedAuthority> roleGranted = role.stream().map(SimpleGrantedAuthority::new).toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username,null,roleGranted);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ExpiredJwtException e) {
                authEntryPoint.commence(request,response,new InsufficientAuthenticationException("Token expired"));
                return;
            } catch (SecurityException e) {
                authEntryPoint.commence(request,response,new InsufficientAuthenticationException("Invalid token"));
                return;
            } catch (Exception e) {
                authEntryPoint.commence(request,response,new InsufficientAuthenticationException("Unhandled error breakdown token"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
