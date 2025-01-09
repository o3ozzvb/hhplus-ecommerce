package kr.hhplus.be.infrastructure.coupon;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.domain.coupon.dto.CouponSearchDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.QCoupon;
import kr.hhplus.be.domain.coupon.entity.QCouponPublish;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.entity.QProductInventory;
import kr.hhplus.be.domain.user.dto.UserCouponDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CouponPubishRepositoryCustomImpl implements CouponPubishRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 보유 쿠폰 목록 조회
     */
    @Override
    public Page<UserCouponDTO> findCouponsBySearchDTO(CouponSearchDTO searchDTO, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QCouponPublish couponPublish = QCouponPublish.couponPublish;

        BooleanBuilder whereClause = new BooleanBuilder();
        if (ObjectUtils.isNotEmpty(searchDTO.getCouponName())) {
            whereClause.and(coupon.couponName.containsIgnoreCase(searchDTO.getCouponName()));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getDiscountType())) {
            whereClause.and(coupon.discountType.eq(searchDTO.getDiscountType()));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getStartDate()) && ObjectUtils.isNotEmpty(searchDTO.getEndDate())) {
            whereClause.and(couponPublish.validStartDate.loe(searchDTO.getEndDate()))
                    .and(couponPublish.validEndDate.goe(searchDTO.getStartDate()));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getStatus())) {
            whereClause.and(couponPublish.status.eq(searchDTO.getStatus()));
        }

        // 페이징 처리된 데이터 조회
        List<UserCouponDTO> coupons = queryFactory
                .select(Projections.constructor(
                        UserCouponDTO.class,
                        couponPublish.id,
                        coupon.id,
                        coupon.couponName,
                        coupon.discountType,
                        coupon.discountValue,
                        couponPublish.publishDate,
                        couponPublish.validStartDate,
                        couponPublish.validEndDate,
                        couponPublish.status,
                        couponPublish.createdAt,
                        couponPublish.updatedAt
                        ))
                .from(coupon)
                .join(couponPublish).on(coupon.id.eq(couponPublish.refCouponId))
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long total = queryFactory
                .selectFrom(coupon)
                .join(couponPublish).on(coupon.id.eq(couponPublish.refCouponId))
                .where(whereClause)
                .fetch().size();

        return new PageImpl<>(coupons, pageable, total);
    }

    @Override
    public Optional<Coupon> findByIdForUpdate(Long couponId) {
        QCoupon coupon = QCoupon.coupon;

        return Optional.ofNullable(
                queryFactory.selectFrom(coupon)
                .where(coupon.id.eq(couponId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // 비관적 락 설정
                .fetchOne()
        );
    }
}
