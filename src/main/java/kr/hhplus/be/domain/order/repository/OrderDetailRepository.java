package kr.hhplus.be.domain.order.repository;

import kr.hhplus.be.domain.order.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository {
    OrderDetail save(OrderDetail orderDetail);

    List<OrderDetail> findByRefOrderId(long orderId);

    void deleteAll();
}
