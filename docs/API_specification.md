# API 명세서

## Swagger API Docs
![api_swagger.png](image%2Fapi_swagger.png)

## 1. POST /api/v1/users/{userId}/charge - 잔액 충전 
 - **Path Variable**
    - userId(`Long`): 사용자 ID  
 - **Request Body**   
   - chargeAmount(`int`) : 충전 금액 `(필수)`
 - **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "balance": 10000
    }
}
```
    
## 2. GET /api/v1/users/{userId}/balance - 잔액 조회
 - **Path Variable**
   - userId(`Long`): 사용자 ID  
 - **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "userId": 1,
        "balance": 10000
    }
}
```

## 3. GET /api/v1/users/{userId}/coupons - 보유 쿠폰목록 조회
 - **Path Variable**
   - userId(`Long`): 사용자 ID
 - **Request Params**
   - couponName(`String`): 쿠폰 명  
   - discountType(`String`): 할인 타입  
     - FIXED_RATE(정률), FIXED_AMOUNT(정액)   
   - startDate(`LocalDate`): 쿠폰 유효일자(시작일)  
   - endDate(`LocalDate`): 쿠폰 유효일자(종료일)  
   - status(`String`): 쿠폰 상태
     - AVAILABLE(사용가능), EXPIRED(만료), REDEEMED(사용완료)
 - **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "couponInfoList": [
            {
                "couponId": 1,
                "couponName": "쿠폰1",
                "publishDate": "2024-12-31",
                "discountType": "FIXED_AMOUNT",
                "discountValue": 20000,
                "validStartDate": "2025-01-01",
                "validEndDate": "2025-01-31",
                "status": "AVAILABLE"
            },
            {
                "couponId": 2,
                "couponName": "쿠폰2",
                "publishDate": "2024-12-31",
                "discountType": "FIXED_AMOUNT",
                "discountValue": 5000,
                "validStartDate": "2025-01-01",
                "validEndDate": "2025-01-31",
                "status": "EXPIRED"
            },
            {
                "couponId": 3,
                "couponName": "쿠폰3",
                "publishDate": "2024-12-25",
                "discountType": "FIXED_AMOUNT",
                "discountValue": 10000,
                "validStartDate": "2025-01-01",
                "validEndDate": "2025-01-31",
                "status": "REDEEMED"
            },
            {
                "couponId": 4,
                "couponName": "쿠폰4",
                "publishDate": "2024-12-30",
                "discountType": "FIXED_RATE",
                "discountValue": 15,
                "validStartDate": "2025-01-01",
                "validEndDate": "2025-01-31",
                "status": "AVAILABLE"
            },
            {
                "couponId": 5,
                "couponName": "쿠폰5",
                "publishDate": "2025-01-01",
                "discountType": "FIXED_RATE",
                "discountValue": 10,
                "validStartDate": "2025-01-01",
                "validEndDate": "2025-01-31",
                "status": "AVAILABLE"
            }
        ]
    }
}
```

## 4. POST /api/v1/coupons/publish - 쿠폰 발급
 - **Request Body**
   - couponId(`long`): 쿠폰 ID `(필수)`
   - userId(`long`): 사용자 ID `(필수)`
   - validStartDate(`LocalDate`): 쿠폰 유효일자(조회범위 시작일) `(필수)`
   - validEndDate(`LocalDate`): 쿠폰 유효일자(조회범위 종료일) `(필수)`
```
EXAMPLE)
 
{
    "couponId": 1,
    "userId": 1,
    "validStartDate": "2025-01-01",
    "validEndDate": "2025-01-31"
}
```
 - **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "couponPublishId": 1
    }
}
```

## 5. GET /api/v1/products - 상품 조회
 - **Reqeust Params**
   - productName(`String`): 상품명
   - startPrice(`int`): 가격 (조회범위 시작가격)
   - endPrice(`int`): 가격 (조회범위 종료가격)
   - category(`String`): 카테고리
     - OUTER(아우터), TOP(상의), PANTS(하의), ACCESSORIES(악세사리), ETC(잡화)
 - **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": [
        {
            "id": 1,
            "productName": "흰색 반팔티",
            "category": "TOP",
            "inventory": 100,
            "price": 45000
        },
        {
            "id": 2,
            "productName": "검은색 반팔티",
            "category": "TOP",
            "inventory": 100,
            "price": 45000
        },
        {
            "id": 3,
            "productName": "청바지",
            "category": "PANTS",
            "inventory": 150,
            "price": 75000
        },
        {
            "id": 4,
            "productName": "나이키 모자",
            "category": "ETC",
            "inventory": 77,
            "price": 43000
        },
        {
            "id": 5,
            "productName": "은색 반지",
            "category": "ACCESSORIES",
            "inventory": 80,
            "price": 38000
        }
    ]
}
```   

## 6. POST /api/v1/orders - 주문 요청
- **Request Body**
  - userId(`long`): 사용자 ID `(필수)`
  - couponPublishId(`long`): 사용할 쿠폰의 발행 ID `(필수)`
  - productIds(`List<Object>`): 주문 상품 정보 (리스트) `(필수)`
    - productId(`long`): 상품 ID `(필수)`
    - quantity(`int`): 상품 주문 수량 `(필수)`
    - price(`int`): 상품 가격 `(필수)`
```
EXAMPLE)

{
    "userId": 1,
    "couponPublishId": 1,
    "orderItemList" : [
        {
            "productId": 1,
            "quantity": 2,
            "price": 50000
        },
        {
            "productId": 2,
            "quantity": 1,
            "price": 33000
        }
    ]
}
```
- **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "orderId": 1,
        "totalAmount": 133000,
        "orderDateTime": "2025-01-02T16:50:01.4748012"
    }
}
```   
## 7. POST /api/v1/payments - 결제 요청
- **Request Body**
  - userId(`long`): 사용자 ID `(필수)`
  - orderId(`long`): 주문 ID `(필수)`
  - payAmount(`int`): 결제 금액 `(필수)`
  - pg(`String`): PG사 (ex. NICEPAY, TOSSPAYMENTS, ...)
- **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": null
}
```   
## 8. GET /api/v1/products/top-sales 상위 상품 목록 조회
- **Response Body**
```
{
    "success": true,
    "message": "요청에 성공하였습니다.",
    "data": {
        "topSalesProducts": [
            {
                "rank": 1,
                "productId": 17,
                "productName": "흰색 반팔티",
                "soldQuantity": 100,
                "totalAmount": 4500000
            },
            {
                "rank": 2,
                "productId": 82,
                "productName": "검은색 반팔티",
                "soldQuantity": 100,
                "totalAmount": 3000000
            },
            {
                "rank": 3,
                "productId": 33,
                "productName": "청바지",
                "soldQuantity": 150,
                "totalAmount": 1750000
            },
            {
                "rank": 4,
                "productId": 24,
                "productName": "나이키 모자",
                "soldQuantity": 77,
                "totalAmount": 990000
            },
            {
                "rank": 5,
                "productId": 15,
                "productName": "은색 반지",
                "soldQuantity": 80,
                "totalAmount": 380000
            }
        ]
    }
}
```