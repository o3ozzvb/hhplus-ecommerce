package kr.hhplus.be.domain.product.repository;

import kr.hhplus.be.domain.product.entity.ProductInventory;

public interface ProductInventoryRepository {
    ProductInventory save(ProductInventory productInventory);

    ProductInventory findById(Long productId);

    ProductInventory findByIdForUpdate(Long productId);
}
