package kr.hhplus.be.infrastructure.coupon.repository;

import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import kr.hhplus.be.infrastructure.coupon.jpa.CouponPublishJpaRepository;
import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponPublishRepositoryImpl implements CouponPublishRepository {

    private final CouponPublishJpaRepository couponPublishJpaRepository;

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
}
