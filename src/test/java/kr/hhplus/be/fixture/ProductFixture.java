package kr.hhplus.be.fixture;

import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;

import java.time.LocalDateTime;

public class ProductFixture {

    public static Product createProduct(String name, Category category, int price) {
        return new Product(null, name, category, price, LocalDateTime.now(), LocalDateTime.now());
    }

    public static ProductInventory createProductInventory(long productId, int inventory) {
        return new ProductInventory(productId, inventory, LocalDateTime.now(), LocalDateTime.now());
    }
}
