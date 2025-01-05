package kr.hhplus.be.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CouponPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refCouponId;

    private Long refUserId;

    private LocalDate publishDate;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private CouponStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
