package com.priv.birdie.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "user_role")
@EqualsAndHashCode(callSuper = true)
public class UserRoleEntity extends BaseAuditEntity {
    @Id
    private Long id;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfoEntity user;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;
    private Boolean isActive;
}
