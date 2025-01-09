package kr.hhplus.be.domain.product.service;

import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * 재고 차감
     */
    void deductInventory(long productId, int quantity);

    /**
     * 재고 추가
     */
    void increaseInventory(long productId, int quantity);

}
