package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.support.exception.CommerceConflictException;
import kr.hhplus.be.support.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    ProductInventoryRepository productInventoryRepository;
    
    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("재고차감 시 차감 수량이 재고보다 크다면 CommerceConflictException이 발생한다.")
    void deductInventory_exception() {
        // given
        long productId = 1L;
        int inventory = 3;
        int quantity = 5;

        ProductInventory productInventory = new ProductInventory(productId, inventory, LocalDateTime.now(), LocalDateTime.now());
        when(productInventoryRepository.findById(productId)).thenReturn(productInventory);

        // when

        // then
        assertThatThrownBy(() -> productService.deductInventory(productId, quantity))
                .isInstanceOf(CommerceConflictException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_INVENTORY.getMessage());
    }
    
    @Test
    @DisplayName("재고차감 시 차감 수량 만큼 재고가 차감된다.")
    void deductInventory() {
        // given
        long productId = 1L;
        int inventory = 15;
        int quantity = 5;

        ProductInventory productInventory = new ProductInventory(productId, inventory, LocalDateTime.now(), LocalDateTime.now());
        when(productInventoryRepository.findById(productId)).thenReturn(productInventory);

        // when
        productService.deductInventory(productId, quantity);
        ProductInventory findProductInventory = productInventoryRepository.findById(productId);

        // then
        assertThat(findProductInventory.getInventory()).isEqualTo(inventory - quantity);
    }

    @Test
    @DisplayName("재고추가 시 추가 수량 만큼 재고가 추가된다.")
    void increaseInventory() {
        // given
        long productId = 1L;
        int inventory = 15;
        int quantity = 5;

        ProductInventory productInventory = new ProductInventory(productId, inventory, LocalDateTime.now(), LocalDateTime.now());
        when(productInventoryRepository.findById(productId)).thenReturn(productInventory);

        // when
        productService.increaseInventory(productId, quantity);
        ProductInventory findProductInventory = productInventoryRepository.findById(productId);

        // then
        assertThat(findProductInventory.getInventory()).isEqualTo(inventory + quantity);
    }

}