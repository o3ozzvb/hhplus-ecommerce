package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.application.platform.PlatformService;
import kr.hhplus.be.domain.order.service.OrderService;
import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.service.PaymentService;
import kr.hhplus.be.domain.user.service.UserService;
import kr.hhplus.be.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PlatformService platformService;

    /**
     * 결제
     * 1. 잔액 확인/차감
     * 2. 결제 정보 저장
     */
    @Transactional
    public Payment payment(PaymentCommand command) {
        Payment payment = null;
        try {
            // 잔액 차감
            userService.useBalance(command.getUserId(), command.getPayAmount());
            // 결제 정보 저장 (결제 성공 처리)
            payment = paymentService.pay(command.getOrderId(), command.getPayAmount());
        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode());
        }
        // 데이터 플랫폼 주문 정보 전송
        OrderInfo orderInfo = orderService.getOrderInfo(command.getOrderId());
        platformService.send(orderInfo);

        return payment;
    }
}
