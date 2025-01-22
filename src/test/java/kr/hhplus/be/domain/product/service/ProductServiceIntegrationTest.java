package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.config.DatabaseCleanup;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ProductServiceIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void cleanUpDatabase() {
        databaseCleanup.execute();
    }

    @Test
    public void 상품목록조회_테스트_전체() {
        //given
        // 상품 데이터 세팅
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop =  productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));

        ProductSearchDTO searchDTO = ProductSearchDTO.builder().build();

        //when
        Page<Product> products = productService.getProducts(searchDTO, PageRequest.of(0, 3));

        //then
        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(6); // 전체 상품 수 6개
        assertThat(products.getTotalPages()).isEqualTo(2);  // 페이지 수: 2페이지
        assertThat(products.getNumber()).isEqualTo(0);  // 현재 페이지 번호: 0
        assertThat(products.getSize()).isEqualTo(3);  // 한 페이지 크기: 3개
        assertThat(products.getContent().size()).isEqualTo(3);  // 실제 조회된 데이터 수: 2개
    }

    @Test
    public void 상품목록조회_테스트_이름조건() {
        //given
        // 상품 데이터 세팅
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop =  productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));

        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .productName("양말")
                .build();

        //when
        Page<Product> products = productService.getProducts(searchDTO, PageRequest.of(0, 2));

        //then
        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(3); // 전체 상품 수 3개
        assertThat(products.getTotalPages()).isEqualTo(2);  // 페이지 수: 2페이지
        assertThat(products.getNumber()).isEqualTo(0);  // 현재 페이지 번호: 0
        assertThat(products.getSize()).isEqualTo(2);  // 한 페이지 크기: 2개
        assertThat(products.getContent().size()).isEqualTo(2);  // 실제 조회된 데이터 수: 2개
    }

    @Test
    public void 상품목록조회_테스트_가격조건() {
        //given
        // 상품 데이터 세팅
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop =  productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, BigDecimal.valueOf(8000)));

        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .startPrice(3500)
                .endPrice(7000)
                .build();

        //when
        Page<Product> products = productService.getProducts(searchDTO, PageRequest.of(0, 2));

        //then
        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(4); // 전체 상품 수 4개
        assertThat(products.getTotalPages()).isEqualTo(2);  // 페이지 수: 2페이지
        assertThat(products.getNumber()).isEqualTo(0);  // 현재 페이지 번호: 0
        assertThat(products.getSize()).isEqualTo(2);  // 한 페이지 크기: 2개
        assertThat(products.getContent().size()).isEqualTo(2);  // 실제 조회된 데이터 수: 2개
    }

    @Test
    @DisplayName("3일간 주문량이 가장 많은 상위 상품 5개가 조회된다.")
    public void 상위_상품목록조회_테스트() {
        //given
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, BigDecimal.valueOf(3000)));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, BigDecimal.valueOf(4000)));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, BigDecimal.valueOf(5000)));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, BigDecimal.valueOf(6000)));
        Product pinkTop =  productRepository.save(createProduct("분홍 티셔츠", Category.TOP, BigDecimal.valueOf(7000)));
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

        //when
        List<TopSalesProductDTO> topSalesProducts = productService.getTopSalesProducts();

        //then
        assertThat(topSalesProducts.size()).isEqualTo(5);
        assertThat(topSalesProducts.get(0))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(new TopSalesProductDTO(whiteSocks.getId(), whiteSocks.getProductName(), whiteSocks.getCategory(), whiteSocks.getPrice(), 20, whiteSocks.getPrice().multiply(BigDecimal.valueOf(20))));
        assertThat(topSalesProducts.get(1))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(new TopSalesProductDTO(whiteTop.getId(), whiteTop.getProductName(), whiteTop.getCategory(), whiteTop.getPrice(), 15, whiteTop.getPrice().multiply(BigDecimal.valueOf(15))));
        assertThat(topSalesProducts.get(2))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(new TopSalesProductDTO(yellowSocks.getId(), yellowSocks.getProductName(), yellowSocks.getCategory(), yellowSocks.getPrice(), 9, yellowSocks.getPrice().multiply(BigDecimal.valueOf(9))));
        assertThat(topSalesProducts.get(3))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(new TopSalesProductDTO(blackSocks.getId(), blackSocks.getProductName(), blackSocks.getCategory(), blackSocks.getPrice(), 8, blackSocks.getPrice().multiply(BigDecimal.valueOf(8))));
        assertThat(topSalesProducts.get(4))
                .usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(new TopSalesProductDTO(pinkTop.getId(), pinkTop.getProductName(), pinkTop.getCategory(), pinkTop.getPrice(), 5, pinkTop.getPrice().multiply(BigDecimal.valueOf(5))));

    }
}
