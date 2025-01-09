package kr.hhplus.be.domain.user.entity;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class BalanceHistory {
    private Long id;
    private Long refUserId;
    private int balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
