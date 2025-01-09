package kr.hhplus.be.domain.payment.service;

import kr.hhplus.be.domain.payment.entity.Payment;

public interface PaymentService {
    /**
     * 결제 (결제 정보 저장)
     */
    Payment pay(long orderId, int payAmount);

}
