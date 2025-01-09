package kr.hhplus.be.infrastructure.coupon;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponPubishRepositoryCustom {

    /** 보유 쿠폰 목록 조회 */
    Page<UserCouponDTO> findCouponsBySearchDTO(CouponSearchDTO searchDTO, Pageable pageable);
}
