package kr.hhplus.be.infrastructure.product.repository;

import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.infrastructure.product.jpa.ProductInventoryJpaRepository;
import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductInventoryRepositoryImpl implements ProductInventoryRepository {

    private final ProductInventoryJpaRepository productInventoryJpaRepository;

    @Override
    public ProductInventory save(ProductInventory productInventory) {
        return productInventoryJpaRepository.save(productInventory);
    }

    @Override
    public ProductInventory findById(Long productId) {
        return productInventoryJpaRepository.findById(productId)
                .orElseThrow(() -> new CommerceNotFoundException(ErrorCode.PRODUCT_INVENTORY_NOT_EXIST));
    }

    @Override
    public ProductInventory findByIdForUpdate(Long productId) {
        return productInventoryJpaRepository.findByIdForUpdate(productId);
    }


}
