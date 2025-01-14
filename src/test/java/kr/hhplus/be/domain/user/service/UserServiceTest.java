package kr.hhplus.be.domain.user.service;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import kr.hhplus.be.domain.user.entity.BalanceHistory;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.BalanceHistoryRepository;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.support.exception.CommerceConflictException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponPublishRepository couponPublishRepository;

    @Mock
    private BalanceHistoryRepository balanceHistoryRepository;

    @Test
    @DisplayName("잔액충전 시 충전금액이 0이면 CommerceConflictException이 발생한다")
    void charge_exception() {
        // given
        long id = 1L;
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.ZERO;

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByIdForUpdate(anyLong())).thenReturn(user);

        // when

        // then
        assertThatThrownBy(() -> userService.charge(id, chargeAmount))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.CHARGE_AMOUNT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("잔액충전 시 충전금액만큼 잔액이 충전된다")
    void charge() {
        // given
        long id = 1L;
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(50000);

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByIdForUpdate(id)).thenReturn(user);
        when(userRepository.findById(id)).thenReturn(user);

        // when
        userService.charge(user.getId(), chargeAmount);

        // then
        User findUser = userRepository.findById(user.getId());
        assertThat(findUser.getBalance()).isEqualTo(balance.add(chargeAmount));
        verify(balanceHistoryRepository, times(1)).save(any(BalanceHistory.class));
    }

    @Test
    @DisplayName("잔액사용 시 잔액이 사용금액보다 적다면 CommerceConflictException이 발생한다")
    void useBalance_exception() {
        // given
        long id = 1L;
        BigDecimal balance = BigDecimal.valueOf(10000);
        BigDecimal useAmount = BigDecimal.valueOf(50000);

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByIdForUpdate(id)).thenReturn(user);

        // when

        // then
        assertThatThrownBy(() -> userService.useBalance(id, useAmount))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("잔액차감 시 사용금액만큼 잔액이 차감된다")
    void useBalance() {
        // given
        long id = 1L;
        BigDecimal balance = BigDecimal.valueOf(10000);
        BigDecimal useAmount = BigDecimal.valueOf(5000);

        User user = new User(id, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByIdForUpdate(id)).thenReturn(user);
        when(userRepository.findById(id)).thenReturn(user);

        // when
        userService.useBalance(id, useAmount);

        // then
        User findUser = userRepository.findById(user.getId());
        assertThat(findUser.getBalance()).isEqualTo(balance.subtract(useAmount));
        verify(balanceHistoryRepository, times(1)).save(any(BalanceHistory.class));
    }

    @Test
    @DisplayName("사용자가 보유한 쿠폰목록이 조회된다.")
    void getUserCouponList() {
        // given
        long userId = 1L;
        UserCouponDTO userCoupon1 = new UserCouponDTO(1L, 1L, "10% 할인 쿠폰", DiscountType.FIXED_RATE, 10, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());
        UserCouponDTO userCoupon2 = new UserCouponDTO(2L, 2L, "10000원 할인 쿠폰", DiscountType.FIXED_AMOUNT, 10000, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());
        UserCouponDTO userCoupon3 = new UserCouponDTO(3L, 3L, "20% 할인 쿠폰", DiscountType.FIXED_RATE, 20, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());
        UserCouponDTO userCoupon4 = new UserCouponDTO(4L, 4L, "5000원 할인 쿠폰", DiscountType.FIXED_AMOUNT, 5000, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(30), CouponPublishStatus.AVAILABLE, LocalDateTime.now(), LocalDateTime.now());

        int page = 0;
        int size = 2;
        List<UserCouponDTO> mockResult = Arrays.asList(userCoupon1, userCoupon2, userCoupon3, userCoupon4);
        CouponSearchDTO searchDTO;

        Pageable pageable = PageRequest.of(0,2);

        when(couponPublishRepository.findUserCouponsBySearchDTO(any(CouponSearchDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(mockResult.subList(0, 2), pageable, mockResult.size()));

        // When
        searchDTO = CouponSearchDTO.builder()
                .userId(userId)
                .build();
        Page<UserCouponDTO> result = userService.getUserCoupons(searchDTO, PageRequest.of(0,2));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(4);  // 전체 쿠폰 수: 4개
        assertThat(result.getTotalPages()).isEqualTo(2);  // 페이지 수: 2페이지
        assertThat(result.getNumber()).isEqualTo(0);  // 현재 페이지 번호: 0
        assertThat(result.getSize()).isEqualTo(2);  // 한 페이지 크기: 2개
        assertThat(result.getContent().size()).isEqualTo(2);  // 실제 조회된 데이터 수: 2개
        assertThat(result.getContent()).containsExactly(userCoupon1, userCoupon2);  // 조회된 데이터 검증
    }
}