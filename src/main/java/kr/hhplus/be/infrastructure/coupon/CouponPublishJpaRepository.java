package kr.hhplus.be.infrastructure.coupon;

import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPublishJpaRepository extends JpaRepository<CouponPublish, Long>, CouponPubishRepositoryCustom {
}
