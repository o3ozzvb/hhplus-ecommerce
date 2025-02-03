package kr.hhplus.be.infrastructure.coupon.jpa;

import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.infrastructure.coupon.custom.CouponPubishRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponPublishJpaRepository extends JpaRepository<CouponPublish, Long>, CouponPubishRepositoryCustom {
    List<CouponPublish> findAllByRefCouponId(long couponId);
}
