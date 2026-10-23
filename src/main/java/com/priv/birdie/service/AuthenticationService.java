package com.priv.birdie.service;

import com.priv.birdie.configuration.JwtUtil;
import com.priv.birdie.configuration.ProcessException;
import com.priv.birdie.entities.*;
import com.priv.birdie.model.AppProperties;
import com.priv.birdie.model.request.LoginRequest;
import com.priv.birdie.model.request.RegisterAccountRequest;
import com.priv.birdie.model.response.LoginResponse;
import com.priv.birdie.repository.*;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.priv.birdie.model.Constant.RespCode.*;
import static com.priv.birdie.model.Constant.Role.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    private final TransactionalService transactionalService;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserInfoRepository userInfoRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginFailedRepository loginFailedRepository;

    @Transactional
    public void registerAccountClient(RegisterAccountRequest request){
        if (accountRepository.existsByEmailOrUsername(request.email(),request.username()))
            throw new ProcessException(HttpStatus.BAD_REQUEST,BAD_REQUEST,"Username or email is already registered");

        AccountUserEntity account = new AccountUserEntity();
        account.setUsername(request.username());
        account.setEmail(request.email());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setIsActive(true);
        account.setTwoAuthActive(request.needTwoAuth() != null && request.needTwoAuth());
        account.setCreatedAt(LocalDateTime.now());
        accountRepository.save(account);

        UserInfoEntity userInfo = new UserInfoEntity();
        userInfo.setAccount(account);
        userInfo.setDisplayName(request.displayName()==null ? request.username() : request.displayName());
        userInfoRepository.save(userInfo);

        RoleEntity role = roleRepository.findByRoleName(USER).orElseThrow();
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setAccountUser(account);
        userRole.setRole(role);
        userRole.setIsActive(true);
        userRole.setCreatedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
    }

    public LoginResponse login(LoginRequest request){
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(),request.password())
            );

            Set<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> Objects.requireNonNull(authority).contains("ROLE"))
                    .collect(Collectors.toSet());

            String token = jwtUtil.generateToken(request.username(),roles);
            Claims claims = jwtUtil.getClaimsFromToken(token);
            transactionalService.deleteFailedLogin(request.username());

            return LoginResponse.builder()
                    .accessToken(token)
                    .refreshToken(buildAndRegisterRefreshToken(request.username()))
                    .expiredAt(
                            claims.getExpiration()
                                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    ).build();
        } catch (UsernameNotFoundException | BadCredentialsException e){
            handleFailedPasswordLogin(request.username());
            throw new ProcessException(HttpStatus.BAD_REQUEST,BAD_CREDENTIAL,"Invalid username / password");
        } catch (LockedException e){
            throw new ProcessException(HttpStatus.UNAUTHORIZED,BAD_CREDENTIAL,"Account locked");
        } catch (DisabledException e){
            throw new ProcessException(HttpStatus.UNAUTHORIZED,BAD_CREDENTIAL,"Account inactive");
        } catch (Exception e){
            throw new ProcessException(HttpStatus.INTERNAL_SERVER_ERROR,INTERNAL_ERROR,"Unknown error");
        }
    }

    private void handleFailedPasswordLogin(String username){
        Optional<AccountUserEntity> account = accountRepository.findByUsernameAndIsActiveTrue(username);

        if (account.isEmpty())
            return;

        LoginFailedEntity failed = new LoginFailedEntity();
        failed.setAccount(account.get());
        failed.setCreatedAt(LocalDateTime.now());
        loginFailedRepository.save(failed);
    }

    public void destroyRefreshToken(String refreshToken){
        if (refreshToken==null || refreshToken.isBlank()){
            log.warn("Refresh token is not provided");
            return;
        }

        RefreshTokenEntity rtEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                ()->new ProcessException(HttpStatus.UNAUTHORIZED,BAD_CREDENTIAL,"Token unknown"));

        if (rtEntity.getRevoked()) {
            log.warn("Refresh token already revoked");
            return;
        }

        rtEntity.setRevoked(true);
        rtEntity.setUpdatedAt(LocalDateTime.now());
        refreshTokenRepository.save(rtEntity);
    }

    private String buildAndRegisterRefreshToken(String username){
        AccountUserEntity account = accountRepository.findByUsernameAndIsActiveTrue(username).orElseThrow();

        String token = CommonUtil.generateUuid();
        long expired = System.currentTimeMillis() + appProperties.getJwt().getRefreshExpired();
        Instant instantExpired = Instant.ofEpochMilli(expired);

        RefreshTokenEntity rToken = new RefreshTokenEntity();
        rToken.setToken(token);
        rToken.setRevoked(false);
        rToken.setCreatedAt(LocalDateTime.now());
        rToken.setAccount(account);
        rToken.setExpiredAt(instantExpired.atZone(ZoneId.systemDefault()).toLocalDateTime());

        refreshTokenRepository.save(rToken);
        return token;
    }

    @Transactional
    public LoginResponse extendAccessToken(String token){
        RefreshTokenEntity rtEntity = refreshTokenRepository.findByToken(token).orElseThrow(
                ()->new ProcessException(HttpStatus.UNAUTHORIZED,BAD_CREDENTIAL,"Token unknown"));
        Long accountId = rtEntity.getAccount().getId();

        if (rtEntity.getRevoked()){ //do something, indicate token has been stolen
            transactionalService.executeIndicateStolenCred(accountId);
            throw new ProcessException(HttpStatus.UNAUTHORIZED,BAD_CREDENTIAL,"Token unknown");
        }

        transactionalService.executeUpdateRevokeToken(rtEntity);

        if (rtEntity.getExpiredAt().isBefore(LocalDateTime.now())){ //do something, destroy & revoked current token
            throw new ProcessException(HttpStatus.BAD_REQUEST,BAD_REQUEST,"Token expired");
        }

        Set<String> roles = userRoleRepository.findByAccountUser_Id(accountId)
                .stream().map(ur->ur.getRole().getRoleName())
                .collect(Collectors.toSet());
        String accessTokenNew = jwtUtil.generateToken(rtEntity.getAccount().getUsername(),roles);
        Claims claims = jwtUtil.getClaimsFromToken(accessTokenNew);

        String tokenNew = CommonUtil.generateUuid();
        long expired = System.currentTimeMillis() + appProperties.getJwt().getRefreshExpired();
        Instant instantExpired = Instant.ofEpochMilli(expired);

        RefreshTokenEntity rtEntityNew = new RefreshTokenEntity();
        rtEntityNew.setToken(tokenNew);
        rtEntityNew.setRevoked(false);
        rtEntityNew.setCreatedAt(LocalDateTime.now());
        rtEntityNew.setAccount(rtEntity.getAccount());
        rtEntityNew.setExpiredAt(instantExpired.atZone(ZoneId.systemDefault()).toLocalDateTime());
        rtEntityNew.setIdReplaced(rtEntity.getId());

        refreshTokenRepository.save(rtEntityNew);

        return LoginResponse.builder()
                .accessToken(accessTokenNew)
                .expiredAt(claims.getExpiration()
                        .toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .refreshToken(tokenNew)
                .build();
    }

}
