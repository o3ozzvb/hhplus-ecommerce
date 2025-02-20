package kr.hhplus.be.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.hhplus.be.application.order.dto.OrderInfo;
import lombok.*;

import java.io.Serializable;

@Getter
public class PaymentSuccessEvent implements Serializable {
    private OrderInfo orderInfo;

    @JsonCreator
    public PaymentSuccessEvent(@JsonProperty("orderInfo") OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
