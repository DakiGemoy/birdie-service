package com.priv.birdie.repository;

import com.priv.birdie.entities.AccountUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountUserEntity, Long> {
    Optional<AccountUserEntity> findByUsername(String username);
    Optional<AccountUserEntity> findByUsernameAndIsActiveTrue(String username);
    Boolean existsByEmailOrUsername(String email, String username);
    @Modifying
    @Query(value = """
            UPDATE account_user
            SET is_active = false,
                updated_at = current_timestamp
            WHERE id = :accId
            """,nativeQuery = true)
    int deactivatedAccountStolen(@Param("accId") Long accId);
}
