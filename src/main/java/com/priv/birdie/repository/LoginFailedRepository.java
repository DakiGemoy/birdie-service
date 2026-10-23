package com.priv.birdie.repository;

import com.priv.birdie.entities.LoginFailedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginFailedRepository extends JpaRepository<LoginFailedEntity, Long> {
    Long countByAccount_Id(Long accountId);
    int deleteByAccount_Id(Long accountId);
}
