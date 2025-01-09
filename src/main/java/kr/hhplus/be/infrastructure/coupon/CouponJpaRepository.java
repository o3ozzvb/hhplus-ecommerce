package kr.hhplus.be.infrastructure.coupon;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long>, CouponPubishRepositoryCustom {
}
