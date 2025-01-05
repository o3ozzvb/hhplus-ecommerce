package kr.hhplus.be.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private int balance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 정적 팩토리 메서드 생성자
    public static User of(String username, int balance) {
        User user = new User();

        user.username = username;
        user.balance = balance;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    /** 잔액 충전 */
    public void charge(int chargeAmount) {
        if (chargeAmount <= 0) {
            throw new BusinessException(ErrorCode.CHARGE_AMOUNT_NOT_VALID);
        }

        this.balance = this.balance + chargeAmount;
    }

    /** 잔액 사용 */
    public void useBalance(int useAmount) {
        if (useAmount > balance) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance = this.balance - useAmount;
    }
}
