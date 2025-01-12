package kr.hhplus.be.infrastructure.product.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.domain.order.entity.QOrder;
import kr.hhplus.be.domain.order.entity.QOrderDetail;
import kr.hhplus.be.domain.product.dto.ProductSearchDTO;
import kr.hhplus.be.domain.product.dto.TopSalesProductDTO;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findProductsBySearchDTO(ProductSearchDTO searchDTO, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanBuilder whereClause = new BooleanBuilder();
        if (StringUtils.isNotBlank(searchDTO.getProductName())) {
            whereClause.and(product.productName.like("%" + searchDTO.getProductName() + "%"));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getCategory())) {
            whereClause.and(product.category.eq(searchDTO.getCategory()));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getStartPrice())) {
            whereClause.and(product.price.goe(searchDTO.getStartPrice()));
        }
        if (ObjectUtils.isNotEmpty(searchDTO.getEndPrice())) {
            whereClause.and(product.price.loe(searchDTO.getEndPrice()));
        }

        // 페이징 처리된 데이터 조회
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long total = queryFactory
                .selectFrom(product)
                .where(whereClause)
                .fetch().size();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public List<TopSalesProductDTO> findTopSalesProducts() {
        QProduct product = QProduct.product;
        QOrder order = QOrder.order;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;

        return queryFactory
                .select(Projections.constructor(TopSalesProductDTO.class,
                        product.id,
                        product.productName,
                        product.category,
                        product.price,
                        orderDetail.quantity.sum().as("soldQuantity"),
                        orderDetail.totalAmount.sum().as("totalAmount")
                ))
                .from(orderDetail)
                .join(product).on(orderDetail.refProductId.eq(product.id))
                .join(order).on(orderDetail.refOrderId.eq(order.id))
                .where(order.orderedAt.after(LocalDateTime.now().minusDays(3)))
                .groupBy(product.id)
                .orderBy(orderDetail.quantity.sum().desc())
                .limit(5)
                .fetch();
    }
}
