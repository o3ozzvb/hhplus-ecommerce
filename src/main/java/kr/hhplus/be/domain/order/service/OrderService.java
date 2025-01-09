package kr.hhplus.be.domain.order.service;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;

import java.util.List;

public interface OrderService {
    /**
     * 주문 생성 (주문정보 저장)
     */
    OrderInfo order(Order order, List<OrderDetail> orderDetails);

    /**
     * 주문 정보 조회
     */
    OrderInfo getOrderInfo(long orderId);
}
