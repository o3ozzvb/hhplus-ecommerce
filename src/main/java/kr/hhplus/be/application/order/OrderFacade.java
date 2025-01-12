package kr.hhplus.be.application.order;

import kr.hhplus.be.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.domain.coupon.service.CouponService;
import kr.hhplus.be.domain.order.service.OrderService;
import kr.hhplus.be.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;

    /**
     * 주문
     * 1. 선택 상품 재고 확인/재고 차감
     * 2. 쿠폰 확인/사용 처리
     * 3. 주문 정보 생성/저장
     */
    @Transactional
    public OrderInfo order(OrderCreateCommand orderCommand) {
        // 재고 차감
        OrderItems orderItems = orderCommand.getOrderItems();
        for (OrderItemInfo orderItem : orderItems.getOrderItems()) {
            productService.deductInventory(orderItem.getProductId(), orderItem.getQuantity());
        }
        // 총 금액, 할인 금액
        int totalAmount = orderItems.getTotalAmount();
        int discountAmount = 0;
        // 쿠폰 사용처리
        if (!ObjectUtils.isEmpty(orderCommand.getCouponPublishId())) {
            couponService.redeemCoupon(orderCommand.getCouponPublishId());
            // 할인 금액 계산
            discountAmount = couponService.getDiscountAmount(orderCommand.getCouponPublishId(), totalAmount);
        }
        // 주문 정보 저장
        OrderInfo orderInfo = orderService.order(orderCommand.toOrder(totalAmount, discountAmount), orderItems.toOrderDetails());

        return orderInfo;
    }


}
