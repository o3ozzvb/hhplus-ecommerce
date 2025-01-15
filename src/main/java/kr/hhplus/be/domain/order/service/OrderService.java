package kr.hhplus.be.domain.order.service;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    /**
     * 주문 생성 (주문정보 저장)
     */
    public OrderInfo order(Order order, List<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrder(order);
        }
        order.setOrderDetails(orderDetails);

        orderRepository.save(order);
        return OrderInfo.from(order);
    }

    /**
     * 주문 정보 조회
     */
    public OrderInfo getOrderInfo(long orderId) {
        Order order = orderRepository.findById(orderId);

        return OrderInfo.from(order);
    }

    /**
     * 주문 완료 처리
     */
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.complete();
        orderRepository.save(order);
    }

    /**
     * 주문 취소 처리
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.cancel();
        orderRepository.save(order);
    }
}
