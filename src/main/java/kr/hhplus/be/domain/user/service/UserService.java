package kr.hhplus.be.domain.user.service;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.user.dto.BalanceDTO;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /**
     * 잔액 충전
     */
    BalanceDTO charge(long userId, int chargeAmount);

    /**
     * 잔액 조회
     */
    BalanceDTO getBalance(long userId);

    /**
     * 잔액 차감
     */
    BalanceDTO useBalance(long userId, int useAmount);

    /**
     * 보유 쿠폰 목록 조회
     */
    Page<UserCouponDTO> getUserCoupons(CouponSearchDTO searchDTO, Pageable pageable);
}
