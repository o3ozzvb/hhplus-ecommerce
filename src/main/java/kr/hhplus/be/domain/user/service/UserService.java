package kr.hhplus.be.domain.user.service;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.user.dto.BalanceDTO;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CouponPublishRepository couponPublishRepository;

    /**
     * 잔액 충전
     */
    BalanceDTO charge(long userId, int chargeAmount) {
        User user = userRepository.findById(userId);
        user.charge(chargeAmount);
        userRepository.save(user);
        return new BalanceDTO(userId, user.getBalance());
    }

    /**
     * 잔액 조회
     */
    BalanceDTO getBalance(long userId) {
        User user = userRepository.findById(userId);
        return new BalanceDTO(userId, user.getBalance());
    }

    /**
     * 잔액 차감
     */
    BalanceDTO useBalance(long userId, int useAmount) {
        User user = userRepository.findById(userId);
        user.useBalance(useAmount);
        userRepository.save(user);
        return new BalanceDTO(userId, user.getBalance());
    }

    /**
     * 보유 쿠폰 목록 조회
     */
    Page<UserCouponDTO> getUserCoupons(CouponSearchDTO searchDTO, Pageable pageable) {
        log.debug("CouponServiceImpl getUserCouponList - searchDTO: {}", searchDTO);

        return couponPublishRepository.findUserCouponsBySearchDTO(searchDTO, pageable);
    }
}
