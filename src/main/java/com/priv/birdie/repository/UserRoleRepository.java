package com.priv.birdie.repository;

import com.priv.birdie.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByAccountUser_Id(Long accountId);
}
