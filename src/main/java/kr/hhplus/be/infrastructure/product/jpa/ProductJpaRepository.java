package kr.hhplus.be.infrastructure.product.jpa;

import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.infrastructure.product.custom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

}
