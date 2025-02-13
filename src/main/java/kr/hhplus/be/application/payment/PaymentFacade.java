package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.domain.event.PaymentSuccessEvent;
import kr.hhplus.be.domain.event.PaymentEventPublisher;
import kr.hhplus.be.domain.order.service.OrderService;
import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.service.PaymentService;
import kr.hhplus.be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PaymentEventPublisher paymentEventPublisher;

    /**
     * 결제
     * 1. 잔액 확인/차감(결제)
     * 2. 결제 정보 저장
     * 3. 주문 상태 변경
     * 4. 주문 정보 데이터플랫폼 전송
     */
    @Transactional
    public Payment payment(PaymentCommand command) {
        // 잔액 차감
        userService.useBalance(command.getUserId(), command.getPayAmount());
        // 결제 정보 저장 (결제 성공 처리)
        Payment payment = paymentService.pay(command.getOrderId(), command.getPayAmount());
        // 주문 상태 변경
        orderService.completeOrder(command.getOrderId());

        // 데이터 플랫폼 주문 정보 전송
        OrderInfo orderInfo = orderService.getOrderInfo(command.getOrderId());
        // 결제 성공 이벤트 발행
        paymentEventPublisher.success(new PaymentSuccessEvent(orderInfo));

        return payment;
    }
}
