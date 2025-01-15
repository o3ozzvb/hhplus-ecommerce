package kr.hhplus.be.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class CouponPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refCouponId;

    private Long refUserId;

    private LocalDate publishDate;

    private LocalDate redeemDate;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    @Enumerated(EnumType.STRING)
    private CouponPublishStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static CouponPublish publishNow(CouponPublishDTO publishDTO) {
        CouponPublish couponPublish = new CouponPublish();

        couponPublish.refCouponId = publishDTO.getCouponId();
        couponPublish.refUserId = publishDTO.getUserId();
        couponPublish.publishDate = LocalDate.now();
        couponPublish.validStartDate = publishDTO.getValidStartDate();
        couponPublish.validEndDate = publishDTO.getValidEndDate();
        couponPublish.status = CouponPublishStatus.AVAILABLE;
        couponPublish.createdAt = LocalDateTime.now();
        couponPublish.updatedAt = LocalDateTime.now();

        return couponPublish;
    }

    /**
     * 쿠폰 사용
     */
    public void redeem() {
        if (!this.status.equals(CouponPublishStatus.AVAILABLE)) {
            throw new CommerceConflictException(ErrorCode.COUPON_NOT_AVAILABLE);
        }
        if (this.validEndDate.compareTo(LocalDate.now()) < 0) {
            throw new CommerceConflictException(ErrorCode.COUPON_VALID_DATE_EXPIRED);
        }
            this.redeemDate = LocalDate.now();
        this.status = CouponPublishStatus.REDEEMED;
        this.updatedAt = LocalDateTime.now();
    }
}
