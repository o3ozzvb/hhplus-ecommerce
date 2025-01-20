package kr.hhplus.be.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refOrderId;

    private Long refProductId;

    private int quantity;

    private BigDecimal price;

    private BigDecimal totalAmount;

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }
}
