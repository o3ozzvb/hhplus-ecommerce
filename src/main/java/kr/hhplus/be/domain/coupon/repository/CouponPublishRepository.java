package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponPublishRepository {
    CouponPublish save(CouponPublish couponPublish);

    CouponPublish findById(long id);

    Page<UserCouponDTO> findUserCouponsBySearchDTO(CouponSearchDTO searchDTO, Pageable pageable);
}
