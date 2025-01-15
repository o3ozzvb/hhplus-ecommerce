package kr.hhplus.be.infrastructure.order.repository;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.infrastructure.order.jpa.OrderDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    private final OrderDetailJpaRepository orderDetailJpaRepository;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailJpaRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> findByOrder(Order findOrder) {
        return orderDetailJpaRepository.findByOrder(findOrder);
    }
}
