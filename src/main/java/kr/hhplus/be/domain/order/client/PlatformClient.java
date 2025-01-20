package kr.hhplus.be.domain.order.client;

import kr.hhplus.be.application.order.dto.OrderInfo;

public interface PlatformClient {
    boolean send(OrderInfo orderInfo);
}
