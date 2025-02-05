package kr.hhplus.be.infrastructure.config;

import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.infrastructure.coupon.redis.CouponRedisRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Profile("!test")
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponRedisRepository couponRedisRepository;
    private final CouponPublishRepository couponPublishRepository;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OrderRepository orderRepository;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(
            UserRepository userRepository,
            CouponRepository couponRepository,
            CouponRedisRepository couponRedisRepository,
            CouponPublishRepository couponPublishRepository,
            ProductRepository productRepository,
            ProductInventoryRepository productInventoryRepository,
            OrderRepository orderRepository,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.couponRedisRepository = couponRedisRepository;
        this.couponPublishRepository = couponPublishRepository;
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.orderRepository = orderRepository;
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

        // AUTO_INCREMENT 초기화
        resetAutoIncrement("users");
        resetAutoIncrement("coupon");
        resetAutoIncrement("coupon_publish");
        resetAutoIncrement("product");
        resetAutoIncrement("orders");

        // 데이터 삽입
        // 사용자 100명
        for (int i = 0; i < 100; i++ ) {
            userRepository.save(User.of("User" + i, BigDecimal.valueOf(10000)));
        }
        // 선착순 발급 쿠폰
        Coupon coupon = couponRepository.save(Coupon.of("선착순 30명 발급 쿠폰", DiscountType.FIXED_AMOUNT, 5000, 30));
        couponRedisRepository.cacheCouponQuantity(coupon.getId(), coupon.getRemainQuantity());
        // 상품 등록
        Product product = productRepository.save(Product.of("양말", Category.ETC, BigDecimal.valueOf(1000)));
        // 재고 등록
        productInventoryRepository.save(ProductInventory.of(product.getId(), 30));

        System.out.println("Database initialized with transactional support.");
    }

    private void resetAutoIncrement(String tableName) {
        String sql = String.format("ALTER TABLE %s AUTO_INCREMENT = 1", tableName);
        jdbcTemplate.execute(sql);
    }
}
