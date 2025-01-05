package kr.hhplus.be.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.domain.product.Category;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.product.ProductInfo;
import kr.hhplus.be.interfaces.dto.product.ProductSelectRequest;
import kr.hhplus.be.interfaces.dto.product.TopSalesProduct;
import kr.hhplus.be.interfaces.dto.product.TopSalesProducts;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    /**
     * 상품 목록 조회
     */
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @GetMapping("/products")
    public ApiResponse<List<ProductInfo>> getProducts(ProductSelectRequest request) {
        List<ProductInfo> productInfos = new ArrayList<>();

        productInfos.add(new ProductInfo(1L, "흰색 반팔티", Category.TOP, 100, 45000));
        productInfos.add(new ProductInfo(2L, "검은색 반팔티", Category.TOP, 100, 45000));
        productInfos.add(new ProductInfo(3L, "청바지", Category.PANTS, 150, 75000));
        productInfos.add(new ProductInfo(4L, "나이키 모자", Category.ETC, 77, 43000));
        productInfos.add(new ProductInfo(5L, "은색 반지", Category.ACCESSORIES, 80, 38000));

        return ApiResponse.success(productInfos);
    }

    /**
     * 상위 상품 목록 조회 (3일 기준, 상위 5개)
     */
    @Operation(summary = "상위 상품 목록 조회", description = "3일 기준 판매량 상위 5개의 상품을 조회합니다.")
    @GetMapping("/products/top-sales")
    public ApiResponse<TopSalesProducts> getTopProducts() {
        List<TopSalesProduct> topSalesProductList = new ArrayList<>();

        topSalesProductList.add(new TopSalesProduct(1,17L, "흰색 반팔티", 100, 4500000));
        topSalesProductList.add(new TopSalesProduct(2,82L, "검은색 반팔티", 100, 3000000));
        topSalesProductList.add(new TopSalesProduct(3,33L, "청바지", 150, 1750000));
        topSalesProductList.add(new TopSalesProduct(4,24L, "나이키 모자", 77, 990000));
        topSalesProductList.add(new TopSalesProduct(5,15L, "은색 반지", 80, 380000));

        return ApiResponse.success(new TopSalesProducts(topSalesProductList));
    }
}
