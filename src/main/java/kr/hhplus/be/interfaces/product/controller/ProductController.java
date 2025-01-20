package kr.hhplus.be.interfaces.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.service.ProductService;
import kr.hhplus.be.interfaces.common.ApiResponse;
import kr.hhplus.be.interfaces.product.dto.ProductResponse;
import kr.hhplus.be.interfaces.product.dto.ProductSelectRequest;
import kr.hhplus.be.interfaces.product.dto.TopSalesProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 조회
     */
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @GetMapping("/products")
    public ApiResponse<Page<ProductResponse>> getProducts(@Valid ProductSelectRequest request) {
        Page<Product> products = productService.getProducts(request.toDTO(), PageRequest.of(request.getPage(), request.getSize()));

        List<Product> productList = products.getContent();
        List<ProductResponse> response = productList.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ApiResponse.success(new PageImpl<>(response, products.getPageable(), products.getTotalElements()));
    }

    /**
     * 상위 상품 목록 조회 (3일 기준, 상위 5개)
     */
    @Operation(summary = "상위 상품 목록 조회", description = "3일 기준 판매량 상위 5개의 상품을 조회합니다.")
    @GetMapping("/products/top-sales")
    public ApiResponse<List<TopSalesProductResponse>> getTopProducts() {
        List<TopSalesProductResponse> topSalesProductResponseList = new ArrayList<>();

        List<TopSalesProductDTO> topSalesProductList = productService.getTopSalesProducts();
        List<TopSalesProductResponse> topSalesProducts = topSalesProductList.stream()
                .map(TopSalesProductResponse::from)
                .collect(Collectors.toList());

        return ApiResponse.success(topSalesProducts);
    }
}
