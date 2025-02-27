# 성능 테스트 계획서

## **1. 개요**

### **목적**
- 전자상거래 시스템의 **핵심 API(상품 조회, 쿠폰 조회, 주문, 결제)의 성능 및 부하 처리 능력 평가**
- **응답 속도, 오류율, 동시 사용자 처리 성능**을 측정하여 최적화 포인트 도출
- **최대 트래픽 한계점을 파악하여 서버 확장 및 성능 개선 방향 수립**

### **테스트 환경**
- **서버 환경:**
    - **API 서버:** `http://ecommerce-app:8080`
    - **DB:** MySQL 8.0
    - **캐싱:** Redis
    - **Auto Scaling 설정:** 없음 (테스트 결과에 따라 적용 검토)
- **테스트 도구:** K6, InfluxDB, Grafana
- **데이터 시각화:** Grafana 대시보드 활용 (`Dashboard ID: 2587`)

---
## **2. 부하 테스트 대상 선정**
### 대상 선정 이유
- 현재 구현된 기능 중 핵심적인 **사용자 트랜잭션**을 담당하는 기능인 **주문과 결제**를 주요 부하 테스트 대상으로 선정하였다. 
또한, 이들 기능과 직접적으로 연계되는 **상품 조회 및 보유 쿠폰 조회 기능**도 포함하여, 실제 사용자 플로우에서 발생할 수 있는 전체적인 서비스 부하를 종합적으로 평가하고자 한다.   


| 테스트 대상 | 설명                        | 
|------------|---------------------------|
| **상품 조회 API (`/api/v1/products`)** | 사용자가 원하는 상품을 검색하고 확인하는 기능 | 
| **보유 쿠폰 조회 API (`/api/v1/users/{userId}/coupons`)** | 적용 가능 쿠폰 조회               |
| **주문 생성 API (`/api/v1/orders`)** | 사용자가 선택한 상품을 주문하는 핵심 기능   | 
| **결제 요청 API (`/api/v1/payments`)** | 결제 프로세스 진행 및 성공 여부 확인     |

**테스트의 초점**
- API별 **응답 속도, 요청 성공률, 서버 부하에 따른 성능 저하 여부** 분석
- **DB 및 캐시 서버 부하 분석 → 주문 처리 시 지연 발생 여부 확인**

---

## 3. 통합 부하 테스트 시나리오
### 단계별 부하 증가 테스트
**목적** 
- 한 번의 테스트 실행으로 기본 성능, 부하 테스트, 스트레스 테스트를 순차적으로 수행하여 최대 부하 한계를 확인  

**테스트 순서** 
1) 초기 성능 테스트 (50명)
2) 부하 테스트 (100명)
3) 스트레스 테스트 (200명)
4) 최대 부하 테스트 (400명)
5) 부하 감소 (서버 복구 확인)

---
## **4. 적합한 테스트 스크립트 작성 및 수행**

### **K6 부하 테스트 스크립트**
```js
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
```

---

## **5. 향후 계획**
- 부하 테스트를 통해 서버의 최대 트래픽 수용 한계점을 확인  
- API별 응답 속도 및 처리량(RPS) 최적화 방향 도출 
- 성능 최적화 방안 모색 및 적용 검토 
- Auto Scaling 및 서버 확장 여부 검토