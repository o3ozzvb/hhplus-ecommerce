package kr.hhplus.be.infrastructure.config;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("!test")
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponPublishRepository couponPublishRepository;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(
            UserRepository userRepository,
            CouponRepository couponRepository,
            CouponPublishRepository couponPublishRepository,
            ProductRepository productRepository,
            ProductInventoryRepository productInventoryRepository,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.couponPublishRepository = couponPublishRepository;
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public CommandLineRunner run() {
        return args -> initializeData();
    }

    @Transactional
    public void initializeData() {
        // 데이터 삭제
        userRepository.deleteAll();
        couponRepository.deleteAll();
        couponPublishRepository.deleteAll();
        productRepository.deleteAll();
        productInventoryRepository.deleteAll();
        orderRepository.deleteAll();
        orderDetailRepository.deleteAll();

        // AUTO_INCREMENT 초기화
        resetAutoIncrement("users");
        resetAutoIncrement("coupon");
        resetAutoIncrement("coupon_publish");
        resetAutoIncrement("product");
        resetAutoIncrement("orders");
        resetAutoIncrement("order_detail");

        // 데이터 삽입
        // 사용자 100명
        for (int i = 0; i < 100; i++ ) {
            userRepository.save(User.of("User" + i, BigDecimal.valueOf(10000)));
        }
        // 선착순 발급 쿠폰
        couponRepository.save(Coupon.of("선착순 30명 발급 쿠폰", DiscountType.FIXED_AMOUNT, 5000, 30));

        // 상품 등록
        Product whiteSocks = productRepository.save(Product.of("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(Product.of("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(Product.of("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(Product.of("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop =  productRepository.save(Product.of("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(Product.of("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));


        // 재고 등록
        productInventoryRepository.save(ProductInventory.of(whiteSocks.getId(), 30));

        // 주문 데이터 세팅
        Order order1 = orderRepository.save(new Order(null, 1L, null, LocalDateTime.now().minusDays(2), BigDecimal.valueOf(106000), BigDecimal.ZERO, BigDecimal.valueOf(106000), OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        Order order2 = orderRepository.save(new Order(null, 2L, null, LocalDateTime.now().minusDays(1), BigDecimal.valueOf(90000), BigDecimal.ZERO, BigDecimal.valueOf(90000), OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        Order order3 = orderRepository.save(new Order(null, 3L, null, LocalDateTime.now(), BigDecimal.valueOf(73000), BigDecimal.ZERO, BigDecimal.valueOf(73000), OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), whiteSocks.getId(), 10, whiteSocks.getPrice(), whiteSocks.getPrice().multiply(BigDecimal.valueOf(10))));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), yellowSocks.getId(), 9, yellowSocks.getPrice(), yellowSocks.getPrice().multiply(BigDecimal.valueOf(9))));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), blackSocks.getId(), 8, blackSocks.getPrice(), blackSocks.getPrice().multiply(BigDecimal.valueOf(8))));
        orderDetailRepository.save(new OrderDetail(null, order2.getId(), whiteSocks.getId(), 10, whiteSocks.getPrice(), whiteSocks.getPrice().multiply(BigDecimal.valueOf(10))));
        orderDetailRepository.save(new OrderDetail(null, order2.getId(), whiteTop.getId(), 10, whiteTop.getPrice(), whiteTop.getPrice().multiply(BigDecimal.valueOf(10))));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), whiteTop.getId(), 5, whiteTop.getPrice(), whiteTop.getPrice().multiply(BigDecimal.valueOf(5))));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), pinkTop.getId(), 5, pinkTop.getPrice(), pinkTop.getPrice().multiply(BigDecimal.valueOf(5))));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), skyblueTop.getId(), 1, skyblueTop.getPrice(), skyblueTop.getPrice().multiply(BigDecimal.valueOf(1))));

        System.out.println("Database initialized with transactional support.");
    }

    private void resetAutoIncrement(String tableName) {
        String sql = String.format("ALTER TABLE %s AUTO_INCREMENT = 1", tableName);
        jdbcTemplate.execute(sql);
    }
}
