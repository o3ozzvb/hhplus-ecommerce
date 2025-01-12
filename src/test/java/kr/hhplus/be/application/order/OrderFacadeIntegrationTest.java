package kr.hhplus.be.application.order;

import kr.hhplus.be.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.entity.CouponPublish;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponPublishRepository;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.fixture.CouponFixture.createCoupon;
import static kr.hhplus.be.fixture.CouponFixture.createCouponPublish;
import static kr.hhplus.be.fixture.ProductFixture.createProduct;
import static kr.hhplus.be.fixture.ProductFixture.createProductInventory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Testcontainers
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPublishRepository couponPublishRepository;

    @Test
    void 주문_통합테스트() {
        // given
        long userId = 1L;
        int socksInitInventory = 20, socksOrderQuantity = 2;
        int topInitInventory = 10, topOrderQuantity = 1;
        // 상품 데이터 세팅
        Product socks = productRepository.save(createProduct("양말", Category.ETC, 5000));
        Product top = productRepository.save(createProduct("티셔츠", Category.TOP, 35000));
        // 상품재고 데이터 세팅
        productInventoryRepository.save(createProductInventory(socks.getId(), socksInitInventory));
        productInventoryRepository.save(createProductInventory(top.getId(), topInitInventory));

        // 주문 정보
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(top.getId(), topOrderQuantity, top.getPrice()));
        orderItemList.add(new OrderItemInfo(socks.getId(), socksOrderQuantity, socks.getPrice()));

        OrderCreateCommand command = OrderCreateCommand.builder()
                        .userId(userId)
                        .orderItems(new OrderItems(orderItemList))
                        .build();

        // when
        OrderInfo orderInfo = orderFacade.order(command);

        // then
        // 생성된 주문 정보 검증
        assertThat(orderInfo.getUserId()).isEqualTo(userId);
        assertThat(orderInfo.getOrderItems().getOrderItems()).hasSize(2)
                .extracting("productId", "quantity", "price")
                .containsExactly(
                        tuple(top.getId(), 1, top.getPrice()),
                        tuple(socks.getId(), 2, socks.getPrice())
                );
        // 상품 재고 차감 검증
        ProductInventory socksInventory = productInventoryRepository.findById(socks.getId());
        ProductInventory topInventory = productInventoryRepository.findById(top.getId());
        assertThat(socksInventory.getInventory()).isEqualTo(socksInitInventory - socksOrderQuantity);
        assertThat(topInventory.getInventory()).isEqualTo(topInitInventory - topOrderQuantity);
    }

    @Test
    void 주문_통합테스트_with쿠폰() {
        // given
        long userId = 1L;

        // 상품 데이터 세팅
        Product socks = productRepository.save(createProduct("양말", Category.ETC, 5000));
        Product top = productRepository.save(createProduct("티셔츠", Category.TOP, 35000));
        // 상품재고 데이터 세팅
        productInventoryRepository.save(createProductInventory(socks.getId(), 20));
        productInventoryRepository.save(createProductInventory(top.getId(), 10));
        // 쿠폰 데이터 세팅
        Coupon coupon = couponRepository.save(createCoupon("10% 할인쿠폰", DiscountType.FIXED_RATE, 10, 30));
        CouponPublish publishedCoupon = couponPublishRepository.save(createCouponPublish(coupon.getId(), userId));
        
        // 주문 정보
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(top.getId(), 1, top.getPrice()));
        orderItemList.add(new OrderItemInfo(socks.getId(), 2, socks.getPrice()));

        OrderCreateCommand command = OrderCreateCommand.builder()
                .userId(userId)
                .couponPublishId(publishedCoupon.getId())
                .orderItems(new OrderItems(orderItemList))
                .build();

        // when
        OrderInfo orderInfo = orderFacade.order(command);

        // then
        int totalAmount = socks.getPrice() * 2 + top.getPrice() * 1;
        int discountAmount = totalAmount * coupon.getDiscountValue() / 100;

        assertThat(orderInfo.getUserId()).isEqualTo(userId);
        assertThat(orderInfo.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(orderInfo.getFinalAmount()).isEqualTo(totalAmount - discountAmount);
        assertThat(orderInfo.getOrderItems().getOrderItems()).hasSize(2)
                .extracting("productId", "quantity", "price")
                .containsExactly(
                        tuple(top.getId(), 1, top.getPrice()),
                        tuple(socks.getId(), 2, socks.getPrice())
                );
    }
}