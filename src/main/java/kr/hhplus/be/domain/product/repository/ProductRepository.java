package kr.hhplus.be.domain.product.repository;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository {

    Product save(Product product);

    @Cacheable(value = "products",
            key = "T(java.lang.String).format('%s_%s_%s_%s_%s_%s_%s', " +
                    "(#searchDTO.productName != null ? #searchDTO.productName : ''), " +
                    "(#searchDTO.startPrice != null ? #searchDTO.startPrice : ''), " +
                    "(#searchDTO.endPrice != null ? #searchDTO.endPrice : ''), " +
                    "(#searchDTO.category != null ? #searchDTO.category : ''), " +
                    "#pageable.pageNumber, " +
                    "#pageable.pageSize, " +
                    "(#pageable.sort != null ? #pageable.sort.toString() : ''))"
    )
    Page<Product> findProductsBySearchDTO(ProductSearchDTO searchDTO, Pageable pageable);

    @Cacheable(value = "topSalesProducts", key = "'topSalesProducts'")
    List<TopSalesProductDTO> findTopSalesProducts();

    void deleteAll();
}
