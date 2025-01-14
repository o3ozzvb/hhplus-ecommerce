package kr.hhplus.be.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.hhplus.be.support.exception.CommerceConflictException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductInventory {

    @Id
    private Long refProductId;

    private int inventory;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ProductInventory of(long refProductId,int inventory) {
        ProductInventory productInventory = new ProductInventory();

        productInventory.refProductId = refProductId;
        productInventory.inventory = inventory;
        productInventory.createdAt = LocalDateTime.now();
        productInventory.updatedAt = LocalDateTime.now();

        return productInventory;
    }

    /** 재고 차감 */
    public void deductInventory(int quantity) {
        if (inventory < quantity) {
            throw new CommerceConflictException(ErrorCode.INSUFFICIENT_INVENTORY);
        }
        this.inventory = this.inventory - quantity;
    }

    /** 재고 추가 */
    public void increaseInventory(int quantity) {
        this.inventory = this.inventory + quantity;
    }
}
