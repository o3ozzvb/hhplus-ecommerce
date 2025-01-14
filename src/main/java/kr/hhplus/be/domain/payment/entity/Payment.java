package kr.hhplus.be.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.payment.enumtype.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refOrderId;

    private LocalDate payDate;

    private BigDecimal payAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String failureReason;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Payment ofSuccess(long orderId, BigDecimal payAmount, String transactionId) {
        Payment payment = new Payment();

        payment.refOrderId = orderId;
        payment.payDate = LocalDate.now();
        payment.payAmount = payAmount;
        payment.status = PaymentStatus.SUCCESS;
        payment.transactionId = transactionId;
        payment.createdAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();

        return payment;
    }
}
