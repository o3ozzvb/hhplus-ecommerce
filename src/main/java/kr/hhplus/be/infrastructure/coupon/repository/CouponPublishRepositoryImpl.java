package kr.hhplus.be.infrastructure.coupon.repository;

import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import kr.hhplus.be.infrastructure.coupon.jpa.CouponPublishJpaRepository;
import kr.hhplus.be.infrastructure.coupon.redis.CouponPublishRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponPublishRepositoryImpl implements CouponPublishRepository {

    private final CouponPublishJpaRepository couponPublishJpaRepository;

    private final CouponPublishRedisRepository couponPublishRedisRepository;

    @Override
    public CouponPublish save(CouponPublish couponPublish) {
        return couponPublishJpaRepository.save(couponPublish);
    }

    @Override
    public CouponPublish findById(long id) {
        return couponPublishJpaRepository.findById(id)
                .orElseThrow(() -> new CommerceNotFoundException(ErrorCode.COUPON_NOT_AVAILABLE));
    }

    @Override
    public Page<UserCouponDTO> findUserCouponsBySearchDTO(CouponSearchDTO searchDTO, Pageable pageable) {
        return couponPublishJpaRepository.findCouponsBySearchDTO(searchDTO, pageable);
    }

    @Override
    public void deleteAll() {
        couponPublishJpaRepository.deleteAll();
    }

    @Override
    public List<CouponPublish> findAllByRefCouponId(long couponId) {
        return couponPublishJpaRepository.findAllByRefCouponId(couponId);
    }

    /**
     * 쿠폰 발급 요청 대기열 저장
     */
    @Override
    public void savePublishRequest(CouponPublishDTO publishDTO) {
        couponPublishRedisRepository.savePublishRequest(publishDTO);
    }

    /**
     * 쿠폰 발급 요청 대기열 꺼내기 (POP)
     */
    @Override
    public List<CouponPublishDTO> getPublishRequest(long couponId, int n) {
        return couponPublishRedisRepository.getPublishRequest(couponId, n);
    }

    /**
     * 쿠폰 발급 유저 조회 (set)
     */
    @Override
    public boolean isCouponPublishRequested(long couponId, long userId) {
        return couponPublishRedisRepository.isCouponPublishRequested(couponId, userId);
    }

    /**
     * 쿠폰 발급 건 수 조회
     */
    @Override
    public Integer getCouponPublishCount(long couponId) {
        return couponPublishRedisRepository.getCouponPublishCount(couponId);
    }

}
