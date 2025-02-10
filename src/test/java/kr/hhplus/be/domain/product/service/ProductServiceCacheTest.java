package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.fixture.ProductFixture.createProduct;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ProductServiceCacheTest {
    @Autowired
    private ProductService productService;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @BeforeEach
    public void setUp() {
        // 기존 데이터 삭제
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 목록 조회 시, 동일한 조건일 경우 여러 번 호출해도 DB는 1번만 조회한다.")
    void getProductsTet() {
        // given
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop = productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));

        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .productName("양말")
                .startPrice(0)
                .endPrice(10000)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        productService.getProducts(searchDTO, pageRequest);
        productService.getProducts(searchDTO, pageRequest);
        productService.getProducts(searchDTO, pageRequest);

        // then
        verify(productRepository, times(1)).findProductsBySearchDTO(searchDTO, pageRequest);
    }

    @Test
    @DisplayName("인기 상품 목록 조회 시, 여러 번 호출해도 DB는 1번만 조회한다.")
    void getTopSalesProductsTet() {
        // given
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop = productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));

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

        // when
        productService.getTopSalesProducts();
        productService.getTopSalesProducts();

        // then
        verify(productRepository, times(1)).findTopSalesProducts();
    }

}


