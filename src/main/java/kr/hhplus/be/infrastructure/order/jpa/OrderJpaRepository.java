package kr.hhplus.be.infrastructure.order.jpa;

import kr.hhplus.be.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository  extends JpaRepository<Order, Long> {
}
