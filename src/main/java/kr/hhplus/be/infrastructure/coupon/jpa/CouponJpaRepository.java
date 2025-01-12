package kr.hhplus.be.infrastructure.coupon.jpa;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.infrastructure.coupon.custom.CouponPubishRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long>, CouponPubishRepositoryCustom {
}
