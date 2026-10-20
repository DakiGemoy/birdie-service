package com.priv.birdie.repository;

import com.priv.birdie.entities.AccountUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountUserEntity, Long> {
}
