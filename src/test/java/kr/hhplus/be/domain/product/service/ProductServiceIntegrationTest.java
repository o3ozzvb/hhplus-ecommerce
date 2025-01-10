package kr.hhplus.be.domain.product.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @Test
    public void 상품목록조회_테스트_전체() {
        //given
        // 상품 데이터 세팅
        productRepository.save(createProduct("하얀 양말", Category.ETC, 5000));
        productRepository.save(createProduct("노란 양말", Category.ETC, 5000));
        productRepository.save(createProduct("검은 양말", Category.ETC, 5000));
        productRepository.save(createProduct("하얀 티셔츠", Category.TOP, 35000));
        productRepository.save(createProduct("분홍 티셔츠", Category.TOP, 35000));
        productRepository.save(createProduct("하늘 티셔츠", Category.TOP, 35000));

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
        productRepository.save(createProduct("하얀 양말", Category.ETC, 5000));
        productRepository.save(createProduct("노란 양말", Category.ETC, 5000));
        productRepository.save(createProduct("검은 양말", Category.ETC, 5000));
        productRepository.save(createProduct("하얀 티셔츠", Category.TOP, 35000));
        productRepository.save(createProduct("분홍 티셔츠", Category.TOP, 35000));
        productRepository.save(createProduct("하늘 티셔츠", Category.TOP, 35000));

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
        productRepository.save(createProduct("하얀 양말", Category.ETC, 3000));
        productRepository.save(createProduct("노란 양말", Category.ETC, 4000));
        productRepository.save(createProduct("검은 양말", Category.ETC, 5000));
        productRepository.save(createProduct("하얀 티셔츠", Category.TOP, 6000));
        productRepository.save(createProduct("분홍 티셔츠", Category.TOP, 7000));
        productRepository.save(createProduct("하늘 티셔츠", Category.TOP, 8000));

        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .startPrice(3500)
                .endPrice(7000)
                .build();

        //when
        Page<Product> products = productService.getProducts(searchDTO, PageRequest.of(0, 2));

        //then
        assertThat(products).isNotNull();
        assertThat(products.getTotalElements()).isEqualTo(4); // 전체 상품 수 3개
        assertThat(products.getTotalPages()).isEqualTo(2);  // 페이지 수: 2페이지
        assertThat(products.getNumber()).isEqualTo(0);  // 현재 페이지 번호: 0
        assertThat(products.getSize()).isEqualTo(2);  // 한 페이지 크기: 2개
        assertThat(products.getContent().size()).isEqualTo(2);  // 실제 조회된 데이터 수: 2개
    }

    @Test
    @DisplayName("3일간 주문량이 가장 많은 상위 상품 5개가 조회된다.")
    public void 상위_상품목록조회_테스트() {
        //given
        Product whiteSocks = productRepository.save(createProduct("하얀 양말", Category.ETC, 3000));
        Product yellowSocks = productRepository.save(createProduct("노란 양말", Category.ETC, 4000));
        Product blackSocks = productRepository.save(createProduct("검은 양말", Category.ETC, 5000));
        Product whiteTop = productRepository.save(createProduct("하얀 티셔츠", Category.TOP, 6000));
        Product pinkTop =  productRepository.save(createProduct("분홍 티셔츠", Category.TOP, 7000));
        Product skyblueTop = productRepository.save(createProduct("하늘 티셔츠", Category.TOP, 8000));

        // 주문 데이터 세팅
        Order order1 = orderRepository.save(new Order(null, 1L, null, LocalDateTime.now().minusDays(2), 106000, 0, 106000, OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        Order order2 = orderRepository.save(new Order(null, 2L, null, LocalDateTime.now().minusDays(1), 90000, 0, 90000, OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        Order order3 = orderRepository.save(new Order(null, 3L, null, LocalDateTime.now(), 73000, 0, 73000, OrderStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), whiteSocks.getId(), 10, whiteSocks.getPrice(), 10 * whiteSocks.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), yellowSocks.getId(), 9, yellowSocks.getPrice(), 9 * yellowSocks.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order1.getId(), blackSocks.getId(), 8, blackSocks.getPrice(), 8 * blackSocks.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order2.getId(), whiteSocks.getId(), 10, whiteSocks.getPrice(), 10 * whiteSocks.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order2.getId(), whiteTop.getId(), 10, whiteTop.getPrice(), 10 * whiteTop.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), whiteTop.getId(), 5, whiteTop.getPrice(), 5 * whiteTop.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), pinkTop.getId(), 5, pinkTop.getPrice(), 5 * pinkTop.getPrice()));
        orderDetailRepository.save(new OrderDetail(null, order3.getId(), skyblueTop.getId(), 1, skyblueTop.getPrice(), 1 * skyblueTop.getPrice()));

        //when
        List<TopSalesProductDTO> topSalesProducts = productService.getTopSalesProducts();

        //then
        assertThat(topSalesProducts.size()).isEqualTo(5);
        assertThat(topSalesProducts.get(0)).extracting("productId", "category", "productName", "price", "soldQuantity", "totalAmount")
                .containsExactly(whiteSocks.getId(), whiteSocks.getCategory(), whiteSocks.getProductName(), whiteSocks.getPrice(), 20, 20 * whiteSocks.getPrice());
        assertThat(topSalesProducts.get(1)).extracting("productId", "category", "productName", "price", "soldQuantity", "totalAmount")
                .containsExactly(whiteTop.getId(), whiteTop.getCategory(), whiteTop.getProductName(), whiteTop.getPrice(), 15, 15 * whiteTop.getPrice());
        assertThat(topSalesProducts.get(2)).extracting("productId", "category", "productName", "price", "soldQuantity", "totalAmount")
                .containsExactly(yellowSocks.getId(), yellowSocks.getCategory(), yellowSocks.getProductName(), yellowSocks.getPrice(), 9, 9 * yellowSocks.getPrice());
        assertThat(topSalesProducts.get(3)).extracting("productId", "category", "productName", "price", "soldQuantity", "totalAmount")
                .containsExactly(blackSocks.getId(), blackSocks.getCategory(), blackSocks.getProductName(), blackSocks.getPrice(), 8, 8 * blackSocks.getPrice());
        assertThat(topSalesProducts.get(4)).extracting("productId", "category", "productName", "price", "soldQuantity", "totalAmount")
                .containsExactly(pinkTop.getId(), pinkTop.getCategory(), pinkTop.getProductName(), pinkTop.getPrice(), 5, 5 * pinkTop.getPrice());

    }
}
