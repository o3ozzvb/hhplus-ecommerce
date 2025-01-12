package kr.hhplus.be.infrastructure.product.custom;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<Product> findProductsBySearchDTO(ProductSearchDTO searchDTO, Pageable pageable);

    List<TopSalesProductDTO> findTopSalesProducts();
}
