package kr.hhplus.be.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity(name = "Orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refUserId;

    private Long refCouponPublishId;

    private LocalDateTime orderedAt;

    private int totalAmount;

    private int discountAmount;

    private int finalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
