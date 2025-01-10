package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static kr.hhplus.be.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ProductServiceIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

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
}
