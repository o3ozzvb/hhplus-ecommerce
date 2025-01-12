package kr.hhplus.be.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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

    private int price;

    private int totalAmount;

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }
}
