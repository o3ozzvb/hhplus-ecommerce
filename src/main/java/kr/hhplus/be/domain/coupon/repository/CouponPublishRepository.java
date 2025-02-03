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
     * 쿠폰 발급 요청 대기열 저장
     */
    public void savePublishRequest(CouponPublishDTO publishDTO);

    /**
     * 쿠폰 발급 요청 대기열 조회
     */
    public List<CouponPublishDTO> getPublishRequest(long couponId, int n);
}
