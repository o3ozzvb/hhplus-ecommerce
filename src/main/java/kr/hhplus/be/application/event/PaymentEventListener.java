package kr.hhplus.be.application.event;

import kr.hhplus.be.domain.event.PaymentSuccessEvent;
import kr.hhplus.be.domain.order.client.PlatformClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PaymentEventListener {

    private final PlatformClient platformClient;

    public PaymentEventListener(PlatformClient platformClient) {
        this.platformClient = platformClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        PaymentSuccessEvent successEvent = new PaymentSuccessEvent(event.getOrderInfo());
        try {
            platformClient.send(event.getOrderInfo());
            log.info("데이터 플랫폼 전송 성공. orderId: {}", event.getOrderInfo().getOrderId());
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패. orderId: {}", event.getOrderInfo().getOrderId(), e);
        }
    }
}
