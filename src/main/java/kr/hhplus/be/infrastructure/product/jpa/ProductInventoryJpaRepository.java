package kr.hhplus.be.infrastructure.product.jpa;

import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.infrastructure.product.custom.ProductInventoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInventoryJpaRepository extends JpaRepository<ProductInventory, Long>, ProductInventoryRepositoryCustom {
}
