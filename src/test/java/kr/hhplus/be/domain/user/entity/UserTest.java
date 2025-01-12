package kr.hhplus.be.domain.user.entity;

import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("충전 금액이 0보다 작거나 같으면 BusinessException이 발생한다")
    void 잔액충전_예외_테스트() {
        //given
        User user = User.of("유저", 0);

        //when

        //then
        assertThatThrownBy(() -> user.charge(0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHARGE_AMOUNT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("잔액이 사용 금액보다 적으면 BusinessException이 발생한다")
    void 잔액사용_예외_테스트() {
        //given
        int balance = 10000;
        User user = User.of("유저", balance);

        //when

        //then
        assertThatThrownBy(() -> user.useBalance(20000))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("잔액 충전 시 충전 금액만큼 잔액이 증가한다.")
    void charge() {
        // given
        User user = User.of("유저", 0);
        int chargeAmount = 1000;

        // when
        user.charge(chargeAmount);

        // then
        assertThat(user.getBalance()).isEqualTo(chargeAmount);
    }

    @Test
    @DisplayName("잔액 사용 시 사용 금액만큼 잔액이 감소한다")
    void decreaseBalance() {
        // given
        int balance = 10000;
        User user = User.of("유저", balance);
        int useAmount = 1000;

        // when
        user.useBalance(useAmount);

        // then
        assertThat(user.getBalance()).isEqualTo(balance - useAmount);
    }
}