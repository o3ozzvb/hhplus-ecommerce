package kr.hhplus.be.infrastructure.order;

import kr.hhplus.be.domain.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByRefOrderId(Long refOrderId);
}
