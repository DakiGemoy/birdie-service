package com.priv.birdie.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class RoleEntity {
    @Id
    private Long id;
    private String roleName;
    private String roleDesc;
    private Boolean isActive;
}
