package kr.hhplus.be.domain.coupon.repository;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponPublishRepository {
    CouponPublish save(CouponPublish couponPublish);

    CouponPublish findById(long id);

    Page<UserCouponDTO> findUserCouponsBySearchDTO(CouponSearchDTO searchDTO, Pageable pageable);

    void deleteAll();

    List<CouponPublish> findAllByRefCouponId(long couponId);

    /**
     * 쿠폰 발급 요청 대기열 저장 & 발급 요청 유저 저장
     */
    void savePublishRequest(CouponPublishDTO publishDTO);

    /**
     * 쿠폰 발급 요청 대기열 조회
     */
    List<CouponPublishDTO> getPublishRequest(long couponId, int n);

    /**
     * 쿠폰 발급 유저 조회 (set)
     */
    boolean isCouponPublishRequested(long couponId, long userId);

    /**
     * 쿠폰 발급 건 수 조회
     */
    Integer getCouponPublishCount(long couponId);

    /**
     * 쿠폰 발급 요청 유저 조회
     */

}
