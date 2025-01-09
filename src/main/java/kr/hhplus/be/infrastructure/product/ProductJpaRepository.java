package kr.hhplus.be.infrastructure.product;

import kr.hhplus.be.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

}
