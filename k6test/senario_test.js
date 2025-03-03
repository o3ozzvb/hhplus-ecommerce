import http from 'k6/http';
import {check, sleep, group, fail } from 'k6';
import {randomItem, randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: "30s", target: 50 },   // 기본 성능 테스트 (50명)
    { duration: "1m", target: 100 },   // 부하 테스트 (100명)
    { duration: "2m", target: 200 },   // 스트레스 테스트 (200명)
    { duration: "2m", target: 400 },   // 최대 부하 한계 테스트 (400명)
    { duration: "30s", target: 0 }     // 부하 감소 (서버 복구 확인)
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
    http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    checks: ["rate>0.99"],             // 99% 이상의 요청이 성공해야 함
  },
};

export default function () {
    group('Load Test Scenario', function () {
        const userId = Math.floor(Math.random() * 10000) + 1;

        // 1. 상품 조회
        const productList = group('Fetch Product List', function () {
            return getProductList(userId);
        });
        if (!productList || productList.length === 0) {
            console.log('No product data.');
            return;
        }

        sleep(2);

        // 2. 보유 쿠폰 조회
        const userCouponList = group('Fetch User Coupon List', function () {
            return getUserCouponList(userId);
        });
        if (!userCouponList || userCouponList.length === 0) {
            console.log('No Coupon data.');
            return;
        }

        sleep(2);

        // 3. 주문
        const orderRes = group('Create Order', function () {
            const couponPublishId = getRandomCoupon(userCouponList);
            const selectedProducts = createOrderItemList(productList);

            return order(userId, couponPublishId, selectedProducts);
        });
        const orderInfo = orderRes.json().data;

        sleep(3);

        // 4. 결제
        group('Payment', function () {
            return payment(userId, orderInfo);
        });
    });
}

function getProductList(userId) {
    const headers = {
        'x-user-id': String(userId)
    };

    const url = `http://ecommerce-app:8080/api/v1/products?page=0&size=10&productName=Product777&category=ETC`;
    const res = http.get(url, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Product Search successful for user ID: ${userId}`);
    } else {
        console.log(`Product Search failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. Product Search failed.`);
    }
    return res;
}

function getUserCouponList(userId) {
    const headers = {
        'x-user-id': String(userId)
    };

    const url = `http://ecommerce-app:8080/api/v1/users/${userId}/coupons?page=0&size=10`;
    const res = http.get(url, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`User Coupon Search successful for user ID: ${userId}`);
    } else {
        console.log(`User Coupon Search failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. User Coupon Search failed.`);
    }
    return res;
}

function order(userId, couponPublishId, orderItemList) {
    const headers = {
        'Content-Type': 'application/json',
        'x-user-id': String(userId)
    };

    const url = `http://ecommerce-app:8080/api/v1/orders`;

    const payload = JSON.stringify({
            userId: userId,
            couponPublishId: null,
            orderItemList: orderItemList
          });

    const res = http.post(url, payload, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Create Order successful for user ID: ${userId}`);
    } else {
        console.log(`Create Order failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. Create Order failed.`);
    }

    return res;
}

function payment(userId, orderInfo) {
    const headers = {
        'Content-Type': 'application/json',
        'x-user-id': String(userId)
    };

    const url = `http://ecommerce-app:8080/api/v1/payments`;

    const payload = JSON.stringify({
            userId: userId,
            orderId : orderInfo.orderId,
            payAmount : orderInfo.totalAmount
          });

    const res = http.post(url, payload, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Payment successful for user ID: ${userId}`);
    } else {
        console.log(`Payment failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. Payment failed.`);
    }
    return res;
}

// 상품 조회 목록 중 랜덤하게 선택해서 배열 만드는 함수
function getRandomProducts(productList) {
    productList = productList.json().data.content;
    if (!productList || productList.length === 0) {
        return [];
    }

    const maxSelectable = Math.min(3, productList.length);
    const numToSelect = Math.floor(Math.random() * maxSelectable) + 1;

    // 상품 목록을 랜덤하게 섞고 원하는 개수만큼 선택
    return productList
        .sort(() => 0.5 - Math.random())  // 무작위 섞기
        .slice(0, numToSelect);
}

// 보유 쿠폰 목록 중 랜덤 선택 함수 (null 포함)
function getRandomCoupon(couponList) {
    couponList = couponList.json().data.content;
    if (!couponList || couponList.length === 0) {
        return null;
    }

    if (Math.random() < 0.3) {
        return null;
    }

    // 랜덤하게 1개의 쿠폰 선택
    return couponList[Math.floor(Math.random() * couponList.length)].couponPublishId;
}

function createOrderItemList(productList) {
    if (!productList || productList.length === 0) {
        return [];
    }

    // 1~3개 랜덤 선택
    const selectedProducts = getRandomProducts(productList);

    // 선택된 상품을 기반으로 orderItemList 생성
    return selectedProducts.map(product => ({
        productId: product.id,
        quantity: 1,  // 기본적으로 1개씩 주문
        price: product.price // 상품 가격 반영
    }));
}

