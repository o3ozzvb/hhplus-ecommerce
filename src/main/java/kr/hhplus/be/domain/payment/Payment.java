package kr.hhplus.be.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refOrderId;

    private LocalDate payDate;

    private int payAmount;

    private PaymentStatus status;

    private String failureReason;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
