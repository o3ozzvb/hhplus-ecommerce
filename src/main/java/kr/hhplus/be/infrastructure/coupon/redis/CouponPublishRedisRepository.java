package kr.hhplus.be.infrastructure.coupon.redis;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;

import java.util.List;

public interface CouponPublishRedisRepository {
    /**
     * 쿠폰 발급 요청 대기열 저장
     */
    public void savePublishRequest(CouponPublishDTO publishDTO);

    /**
     * 쿠폰 발급 요청 대기열 조회
     */
    public List<CouponPublishDTO> getPublishRequest(long couponId, int n);
}
