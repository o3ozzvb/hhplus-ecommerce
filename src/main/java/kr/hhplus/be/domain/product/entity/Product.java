package kr.hhplus.be.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.domain.product.enumtype.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private int price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
