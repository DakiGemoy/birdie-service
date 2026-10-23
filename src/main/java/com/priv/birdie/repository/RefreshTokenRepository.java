package com.priv.birdie.repository;

import com.priv.birdie.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    @Modifying
    @Query(value = """
            UPDATE refresh_token
            SET revoked = true,
                updated_at = current_timestamp
            WHERE account_id = :accId AND revoked = false
            """,nativeQuery = true)
    int revokedAllRefreshTokenByAccountId(@Param("accId") Long accountId);
}
