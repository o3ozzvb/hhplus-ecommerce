package kr.hhplus.be.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 정적 팩토리 메서드 생성자
    public static User of(String username, BigDecimal balance) {
        User user = new User();

        user.username = username;
        user.balance = balance;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    /** 잔액 충전 */
    public void charge(BigDecimal chargeAmount) {
        if (chargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CommerceConflictException(ErrorCode.CHARGE_AMOUNT_NOT_VALID);
        }

        this.balance = this.balance.add(chargeAmount);
    }

    /** 잔액 사용 */
    public void useBalance(BigDecimal useAmount) {
        if (balance.compareTo(useAmount) <= 0) {
            throw new CommerceConflictException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance = this.balance.subtract(useAmount);
    }
}
