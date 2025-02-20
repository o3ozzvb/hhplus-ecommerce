package kr.hhplus.be.interfaces.payment.scheduler;

import kr.hhplus.be.application.outbox.PaymentOutboxFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOutboxScheduler {

    private final PaymentOutboxFacade outboxFacade;

    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    public void publishPaymentSuccessEvent() {
        outboxFacade.publishUnsuccssedEvents();
    }

}
