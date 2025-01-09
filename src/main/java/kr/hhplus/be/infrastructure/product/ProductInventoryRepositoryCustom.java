package kr.hhplus.be.infrastructure.product;

import kr.hhplus.be.domain.product.entity.ProductInventory;

public interface ProductInventoryRepositoryCustom {
    ProductInventory findByIdForUpdate(Long productId);
}
