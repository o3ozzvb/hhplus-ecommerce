package kr.hhplus.be.domain.product;

import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductInventoryTest {

    @Test
    @DisplayName("재고차감 시 기존재고가 차감재고보다 적다면 BusinessException이 발생한다")
    void 재고차감_예외_테스트() {
        // given
        int inventory = 20;
        int deductQuantity = 100;
        ProductInventory productInventory = ProductInventory.of(1L, inventory);

        // when

        //then
        assertThatThrownBy(() -> productInventory.deductInventory(deductQuantity))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_INVENTORY.getMessage());
    }

    @Test
    @DisplayName("재고차감 시 차감한 재고만큼 수량이 감소한다")
    void deductInventory() {
        // given
        int inventory = 20;
        int deductQuantity = 10;
        ProductInventory productInventory = ProductInventory.of(1L, inventory);

        // when
        productInventory.deductInventory(deductQuantity);

        // then
        assertThat(productInventory.getInventory()).isEqualTo(inventory - deductQuantity);
    }

    @Test
    @DisplayName("재고추가 시 증가한 재고만큼 수량이 증가한다")
    void increaseInventory() {
        // given
        int inventory = 20;
        int increaseQuantity = 10;
        ProductInventory productInventory = ProductInventory.of(1L, inventory);

        // when
        productInventory.increaseInventory(increaseQuantity);

        // then
        assertThat(productInventory.getInventory()).isEqualTo(inventory + increaseQuantity);
    }
}