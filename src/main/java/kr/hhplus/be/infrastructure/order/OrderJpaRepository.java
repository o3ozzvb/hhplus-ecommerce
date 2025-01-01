package kr.hhplus.be.infrastructure.order;

import kr.hhplus.be.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository  extends JpaRepository<Order, Long> {
}
