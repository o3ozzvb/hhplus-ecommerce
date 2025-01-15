package kr.hhplus.be.repository;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Test
    void 주문_저장(){
        // given
        long userId = 1L;
        Order order = Order.of(1L, null, BigDecimal.valueOf(10000), BigDecimal.ZERO, OrderStatus.PENDING);
        order.addOrderDetail(new OrderDetail(null, order, 1L, 1, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)));
        order.addOrderDetail(new OrderDetail(null, order, 2L, 1, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)));

        // when
        orderRepository.save(order);

        // then
        Order findOrder = orderRepository.findById(order.getId());
        assertThat(findOrder).isNotNull();
        List<OrderDetail> findOrderDetails = orderDetailRepository.findByOrder(findOrder);
        assertThat(findOrderDetails).hasSize(2);

    }
}
