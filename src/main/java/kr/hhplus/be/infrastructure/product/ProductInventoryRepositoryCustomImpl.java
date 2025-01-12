package kr.hhplus.be.infrastructure.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.entity.QProductInventory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductInventoryRepositoryCustomImpl implements ProductInventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProductInventory findByIdForUpdate(Long productId) {
        QProductInventory productInventory = QProductInventory.productInventory;

        return queryFactory.selectFrom(productInventory)
                .where(productInventory.refProductId.eq(productId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // 비관적 락 설정
                .fetchOne();
    }
}
