package kr.hhplus.be.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Product of(String productName, Category category, BigDecimal price) {
        Product product = new Product();

        product.productName = productName;
        product.category = category;
        product.price = price;
        product.createdAt = LocalDateTime.now();
        product.updatedAt = LocalDateTime.now();

        return product;
    }
}
