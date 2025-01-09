package kr.hhplus.be.domain.user;

import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.domain.user.service.UserServiceImpl;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("잔액충전 시 충전금액이 0이면 BusinessException이 발생한다")
    void charge_exception() {
        // given
        long id = 1L;
        int balance = 1000;
        int chargeAmount = 0;

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(user);

        // when

        // then
        assertThatThrownBy(() -> userService.charge(id, chargeAmount))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHARGE_AMOUNT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("잔액충전 시 충전금액만큼 잔액이 충전된다")
    void charge() {
        // given
        long id = 1L;
        int balance = 1000;
        int chargeAmount = 50000;

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(user);

        // when
        user.charge(chargeAmount);

        // then
        User findUser = userRepository.findById(user.getId());
        assertThat(findUser.getBalance()).isEqualTo(balance + chargeAmount);

    }

    @Test
    @DisplayName("잔액사용 시 잔액이 사용금액보다 적다면 BusinessException이 발생한다")
    void useBalance_exception() {
        // given
        long id = 1L;
        int balance = 10000;
        int useAmount = 50000;

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(user);

        // when

        // then
        assertThatThrownBy(() -> userService.useBalance(id, useAmount))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("잔액차감 시 사용금액만큼 잔액이 차감된다")
    void useBalance() {
        // given
        long id = 1L;
        int balance = 10000;
        int useAmount = 5000;

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(user);

        // when
        userService.useBalance(id, useAmount);

        // then
        when(userRepository.findById(id)).thenReturn(user);
        User findUser = userRepository.findById(user.getId());
        assertThat(findUser.getBalance()).isEqualTo(balance - useAmount);
    }


}