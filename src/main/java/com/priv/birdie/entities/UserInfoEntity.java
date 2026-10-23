package com.priv.birdie.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_info")
public class UserInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private AccountUserEntity account;
    private String displayName;
    private String biography;
    private LocalDateTime updatedAt;
}
