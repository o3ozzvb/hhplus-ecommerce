package kr.hhplus.be.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long refUserId;
    private int balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BalanceHistory from(User user) {
        BalanceHistory balanceHistory = new BalanceHistory();

        balanceHistory.refUserId = user.getId();
        balanceHistory.balance = user.getBalance();
        balanceHistory.createdAt = user.getCreatedAt();
        balanceHistory.updatedAt = user.getUpdatedAt();

        return balanceHistory;
    }
}
