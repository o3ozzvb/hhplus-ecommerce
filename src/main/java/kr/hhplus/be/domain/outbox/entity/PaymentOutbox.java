package kr.hhplus.be.domain.outbox.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.outbox.enumtype.OutboxStatus;
import kr.hhplus.be.support.util.JsonUtil;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentOutbox {

    @Id
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static PaymentOutbox of(String messageId, OutboxStatus status, Object payload) {
        PaymentOutbox outbox = new PaymentOutbox();
        outbox.messageId = messageId;
        outbox.status = status;
        outbox.payload = JsonUtil.toJson(payload);
        outbox.createdAt = LocalDateTime.now();

        return outbox;
    }

    public PaymentOutbox success() {
        this.status = OutboxStatus.SUCCESS;
        return this;
    }

    public PaymentOutbox fail() {
        this.status = OutboxStatus.FAIL;
        return this;
    }
}
