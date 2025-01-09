package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    /**
     * 재고 차감
     */
    @Override
    public void deductInventory(long productId, int quantity) {
        // 상품 재고 조회
        ProductInventory productInventory = productInventoryRepository.findByIdForUpdate(productId);
        // 재고 차감
        productInventory.deductInventory(quantity);
        productInventoryRepository.save(productInventory);
    }

    /**
     * 재고 추가
     */
    @Override
    public void increaseInventory(long productId, int quantity) {
        // 상품 재고 조회
        ProductInventory productInventory = productInventoryRepository.findByIdForUpdate(productId);
        // 재고 추가
        productInventory.increaseInventory(quantity);
    }

    /**
     * 상품 목록 조회
     */
    @Override
    public Page<Product> getProducts(ProductSearchDTO searchDTO, Pageable pageable) {
        return productRepository.findProductsBySearchDTO(searchDTO, pageable);
    }

    /**
     * 상위 상품 목록 조회
     */
    @Override
    public List<TopSalesProductDTO> getTopSalesProducts() {
        return productRepository.findTopSalesProducts();
    }
}
