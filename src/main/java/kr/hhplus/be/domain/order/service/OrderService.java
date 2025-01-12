package kr.hhplus.be.domain.order.service;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    /**
     * 주문 생성 (주문정보 저장)
     */
    public OrderInfo order(Order order, List<OrderDetail> orderDetails) {
        Order savedOrder = orderRepository.save(order);

        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setRefOrderId(savedOrder.getId());
            orderDetailRepository.save(orderDetail);
        }

        return OrderInfo.from(order, orderDetails);
    }

    /**
     * 주문 정보 조회
     */
    public OrderInfo getOrderInfo(long orderId) {
        Order order = orderRepository.findById(orderId);
        List<OrderDetail> orderDetails = orderDetailRepository.findByRefOrderId(orderId);

        return OrderInfo.from(order, orderDetails);
    }
}
