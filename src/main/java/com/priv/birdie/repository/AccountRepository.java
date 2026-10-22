package com.priv.birdie.repository;

import com.priv.birdie.entities.AccountUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountUserEntity, Long> {
    Optional<AccountUserEntity> findByUsernameAndIsActiveTrue(String username);
}
