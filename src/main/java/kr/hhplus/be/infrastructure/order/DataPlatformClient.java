package kr.hhplus.be.infrastructure.order;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.domain.order.client.PlatformClient;
import org.springframework.stereotype.Component;

@Component
public class DataPlatformClient implements PlatformClient {
    @Override
    public boolean send(OrderInfo orderInfo) {
        // 주문 정보 전송 ~
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
