package kr.hhplus.be.infrastructure.order.repository;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.infrastructure.order.jpa.OrderJpaRepository;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_EXIST));
    }
}
