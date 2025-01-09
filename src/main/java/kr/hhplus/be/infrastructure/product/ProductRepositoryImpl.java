package kr.hhplus.be.infrastructure.product;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Page<Product> findProductsBySearchDTO(ProductSearchDTO searchDTO, Pageable pageable) {
        return productJpaRepository.findProductsBySearchDTO(searchDTO, pageable);
    }

    @Override
    public List<TopSalesProductDTO> findTopSalesProducts() {
        return productJpaRepository.findTopSalesProducts();
    }
}
