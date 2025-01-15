package kr.hhplus.be.domain.order.entity;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "ref_order_id", nullable = false)
    private Order order;

    private Long refProductId;

    private int quantity;

    private BigDecimal price;

    private BigDecimal totalAmount;

    public void setOrder(Order order) {
        this.order = order;
    }
}
