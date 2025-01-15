package kr.hhplus.be.domain.order.repository;

import kr.hhplus.be.domain.order.entity.Order;

public interface OrderRepository {
    Order save(Order order);

    Order findById(Long id);

    Order findByIdWithOrderDetails(Long id);
}
