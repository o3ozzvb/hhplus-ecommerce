package kr.hhplus.be.application.platform;

import kr.hhplus.be.application.order.dto.OrderInfo;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {
    
    public boolean send(OrderInfo orderInfo) {
        // 주문 정보 전송 ~
        return true;
    }
}
