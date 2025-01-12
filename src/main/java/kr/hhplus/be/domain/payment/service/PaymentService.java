package kr.hhplus.be.domain.payment.service;

public interface PaymentService {
    /**
     * 결제 (결제 정보 저장)
     */
    void pay(long orderId, int payAmount);

}
