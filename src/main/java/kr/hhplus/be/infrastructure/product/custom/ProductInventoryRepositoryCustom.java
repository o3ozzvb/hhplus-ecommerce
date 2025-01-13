package kr.hhplus.be.infrastructure.product.custom;

import kr.hhplus.be.domain.product.entity.ProductInventory;

public interface ProductInventoryRepositoryCustom {
    ProductInventory findByIdForUpdate(Long productId);
}
