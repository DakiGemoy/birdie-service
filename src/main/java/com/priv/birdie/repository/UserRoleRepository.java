package com.priv.birdie.repository;

import com.priv.birdie.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByAccountUser_Id(Long accountId);
    @Modifying
    @Query(value = """
            UPDATE user_role
            SET is_active = false,
                updated_at = current_timestamp
            WHERE account_id = :accId
            """, nativeQuery = true)
    int deactivatedUserRoleAccess(@Param("accId") Long accountId);
}
