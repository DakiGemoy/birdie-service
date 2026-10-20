package com.priv.birdie.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "account_user")
@EqualsAndHashCode(callSuper = true)
public class AccountUserEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean twoAuthActive;
    private String password;
}
