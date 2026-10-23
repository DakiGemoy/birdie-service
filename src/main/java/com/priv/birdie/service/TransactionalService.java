package com.priv.birdie.service;

import com.priv.birdie.entities.RefreshTokenEntity;
import com.priv.birdie.repository.AccountRepository;
import com.priv.birdie.repository.RefreshTokenRepository;
import com.priv.birdie.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionalService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRoleRepository userRoleRepository;
    private final AccountRepository accountRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void executeIndicateStolenCred(Long accountId){
        int tokenRevoked = refreshTokenRepository.revokedAllRefreshTokenByAccountId(accountId);
        log.info("Success revoked all token : {} token",tokenRevoked);
        int accountDeactivated = accountRepository.deactivatedAccountStolen(accountId);
        log.info("Success deactivated account : {} account",accountDeactivated);
        int userRoleDeactivated = userRoleRepository.deactivatedUserRoleAccess(accountId);
        log.info("Success deactivated user-role : {} account",userRoleDeactivated);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void executeUpdateRevokeToken(RefreshTokenEntity rToken){
        rToken.setRevoked(true);
        rToken.setUpdatedAt(LocalDateTime.now());
        refreshTokenRepository.save(rToken);

        log.info("Success update token to revoked");
    }

}
