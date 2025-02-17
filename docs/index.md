# 인덱스 설계

## 1. 인덱스란
인덱스의 사전적 정의는 다음과 같습니다.
> **색인(index)** 은 어떤 내용을 빨리 찾아보기 위해 먼저 보는 표를 말합니다. 책, 데이터베이스, 웹 등 다양한 영역에서 사용됩니다.

- 인덱스는 지정한 컬럼들을 기준으로 메모리 영역에 일종의 목차를 생성하는 것입니다.
- 인덱스 사용 시 **INSERT, UPDATE, DELETE** 등의 Command 성능은 희생되지만, **SELECT** (Query)의 성능은 향상됩니다.

## 2. Clustered Key 와 Non Clustered Key

| 구분               | 대상 및 특징                                                                                                                          | 제한                           |
|--------------------|-------------------------------------------------------------------------------------------------------|------------------------------|
| **Clustered Key**  | 1) 기본키(PK) <br> 2) PK가 없을 경우 Unique Key <br> 3) PK, Unique Key가 모두 없을 경우 6byte의 Hidden Key (rowid) 생성 <br> | 테이블 당 1개만 존재                 |
| **Non Clustered Key** | 일반적인 인덱스 <br> | 여러 개 생성 가능                   |

## 3. 인덱스를 사용하면 좋은 케이스

- 데이터의 양이 많고, 검색(SELECT)이 변경(INSERT, UPDATE, DELETE)보다 잦은 경우
- 규모가 작지 않은 테이블
- **INSERT, UPDATE, DELETE**가 자주 발생하지 않는 컬럼
- **JOIN, WHERE, ORDER BY**에 자주 사용되는 컬럼
- 데이터의 중복도가 낮은 컬럼
- 인덱스 대상 컬럼의 값이 다양한 경우

## 4. 인덱스 구성 및 사용 시 고려할 점
- 조회 쿼리에서 인덱스를 제대로 사용하려면, 인덱스의 선두 컬럼이 **반드시** 조회 조건에 포함되어야 합니다.
- UPDATE와 DELETE가 빈번하게 발생하는 컬럼에 인덱스를 추가하면, 인덱스 유지 비용으로 인해 성능이 오히려 저하될 수 있습니다.
- 잘못 구성된 인덱스는 오히려 전체 성능에 역효과를 줄 수 있습니다.

---
## 5. 이커머스 서비스 내 쿼리 수집 및 인덱스 설정

### 5.1. 인기상품 목록 조회 (3일 기준 상위 5개)
#### 5.1.1 조회 쿼리 
- 최근 3일간 완료된 주문에서 각 상품의 주문 수량을 집계하여 인기 상품 5개를 조회하는 쿼리는 다음과 같습니다.
  ```
  SELECT P.ID, P.PRODUCT_NAME, P.CATEGORY, SUM(OD.QUANTITY) AS TOTAL_SOLD_QUANTITY
  FROM PRODUCT P  
  JOIN ORDER_DETAIL OD ON P.ID = OD.REF_PRODUCT_ID  
  JOIN ORDERS O ON OD.REF_ORDER_ID = O.ORDER_ID  
  WHERE O.STATUS = 'COMPLETED'  
  AND O.ORDERED_AT > ?  -- (현재일자 3일 전)
  GROUP BY P.ID
  ORDER BY TOTAL_SOLD_QUANTITY DESC  
  LIMIT 5;
  ```
#### 5.1.2 인덱스 설계
1. **ORDERS 테이블**  
  ```
  CREATE INDEX idx_order_status_orderedat ON ORDERS (STATUS, ORDERED_AT);
  ```
  - WHERE 절의 STATUS와 ORDERED_AT을 복합 인덱스로 구성합니다.

2. **ORDER_DETAIL 테이블**  
  ```
  CREATE INDEX idx_orderdetail_refproductid_orderid_quantity ON ORDER_DETAIL (REF_PRODUCT_ID, REF_ORDER_ID, QUANTITY);
  ```
 - REF_PRODUCT_ID (선두 컬럼): PRODUCT와의 조인 및 GROUP BY 조건에 직접 사용되므로, 가장 먼저 인덱스에서 검색할 수 있게 합니다.
 - REF_ORDER_ID : ORDERS 테이블과의 조인에 사용됩니다. 인덱스의 두 번째 컬럼으로 포함하면, 특정 상품(REF_PRODUCT_ID)에 속한 주문 상세 정보 중 해당 주문의 연결을 빠르게 할 수 있습니다.
 - QUANTITY : 집계 함수 SUM(OD.QUANTITY)에 사용되므로, 인덱스에 포함하면 인덱스만으로도 집계 결과를 얻을 수 있는 **커버링 인덱스** 역할을 할 수 있습니다.
> **커버링 인덱스 (Covering Index)**   
> 인덱스에 포함된 컬럼값들로만 쿼리를 완성할 수 있다면, 실제 데이터 레코드를 읽지 않아도 되어 성능이 향상됩니다.

### 5.1.3 성능 비교 및 개선
1-1. 테스트 환경  
- MySql 8.0 컨테이너 2개 띄워, 동일한 양의 데이터 셋팅.
- 데이터 :
   - PRODUCT - 10만 건 
   - ORDERS - 150만 건
   - ORDER_DETAIL - 500만 건

1-2. 성능 비교  

| 항목          | 1회   | 2회   | 3회   | 4회   | 5회   | 6회   | 7회   | 8회   | 9회   | 10회  | 평균   |
|-------------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| **인덱스 생성**  | 89.43 | 144.42 | 152.85 | 112.60 | 143.78 | 129.08 | 186.16 | 151.10 | 125.92 | 145.12 | 138.05 |
| **인덱스 미생성** | 62.81 | 79.94  | 63.95  | 59.53  | 68.80  | 69.29  | 67.38  | 63.80  | 66.54  | 70.24  | 67.23  |
 - 인덱스를 생성했을 때의 성능이 인덱스를 생성하지 않았을 때의 성능보다 현저히 낮게 나타났습니다. 원인을 파악하기 위해 Explain Plan 을 분석해보았습니다.

1-3. 실행 계획 분석
```
→ Limit: 5 row(s) (actual time=49573..49573 rows=5 loops=1)
  → Sort: TOTAL_SOLD_QUANTITY DESC, limit input to 5 row(s) per chunk (actual time=49573..49573 rows=5 loops=1)
    → Table scan on <temporary> (actual time=49573..49573 rows=999 loops=1)
      → Aggregate using temporary table (actual time=49573..49573 rows=999 loops=1)
        → Nested loop inner join (cost=3.5e+6 rows=240984) (actual time=210..49393 rows=11281 loops=1)
          → Nested loop inner join (cost=3.23e+6 rows=240984) (actual time=60.3..49366 rows=11296 loops=1)
            → Filter: ((od.ref_order_id is not null) and (od.ref_product_id is not null)) (cost=508783 rows=4.82e+6) (actual time=2.78..3736 rows=5e+6 loops=1)
              → Covering index scan on OD using idx_orderdetail_refproductid_orderid_quantity (cost=508783 rows=4.82e+6) (actual time=2.77..2873 rows=5e+6 loops=1)
            → Filter: ((o.`status` = 'COMPLETED') and (o.ordered_at > <cache>((now() - interval 3 day)))) (cost=0.465 rows=0.05) (actual time=0.00897..0.00897 rows=0.00226 loops=5e+6)
              → Single-row index lookup on O using PRIMARY (id=od.ref_order_id) (cost=0.465 rows=1) (actual time=0.00837..0.0084 rows=1 loops=5e+6)
          → Single-row index lookup on P using PRIMARY (id=od.ref_product_id) (cost=0.997 rows=1) (actual time=0.002..0.00203 rows=0.999 loops=11296)
```
- **Nested loop inner join (OD → O → P)** 
  - **OD 인덱스 스캔:**   
  500만 건을 커버링 인덱스로 조회 (실시간 2.77~2873ms까지 소요)
  - **O 테이블 PK Lookup (id = od.ref_order_id):**     
  **500만 번 반복(loops=5e+6)**  -> *성능 저하의 원인!!*  
  status='COMPLETED' & ordered_at 필터
- 분석 결과
  - 주문상세 500만 건을 모두 스캔하고 각 행마다 주문 테이블을 조회하여 필터링하는 전형적인 Nested Loop 패턴으로
    대량의 반복 조인 때문에 쿼리 시간이 길어짐(약 49.57초).
- 개선 방향
  - 주문(Order) → 주문상세(OrderDetail) → 상품(Product) 순으로 조인하도록 쿼리를 재작성하여 주문 테이블에서 먼저 상태와 기간으로 필터링 후, 필요한 주문상세만 탐색하도록 합니다.   

2-1. 개선 (쿼리 수정 & 인덱스 보강)
- 수정 쿼리 (테이블 JOIN 순서 변경) : Order -> OrderDetail -> Product
```
SELECT P.ID,
       P.PRODUCT_NAME,
       P.CATEGORY,
       P.PRICE,
       SUM(OD.QUANTITY) AS TOTAL_SOLD_QUANTITY
  FROM ORDERS O
  JOIN ORDER_DETAIL OD  ON O.ID = OD.REF_ORDER_ID
  JOIN PRODUCT P        ON P.ID = OD.REF_PRODUCT_ID 
 WHERE O.STATUS = 'COMPLETED'
   AND O.ORDERED_AT > ?
 GROUP BY P.ID
 ORDER BY TOTAL_SOLD_QUANTITY DESC
 LIMIT 5;
```
- 인덱스 추가
```
CREATE INDEX idx_orderdetail_orderid_productid ON order_detail (ref_order_id, ref_product_id);
```
  - '주문 ID'로 `ORDER_DETAIL`을 조인할 때, 선두 컬럼이 'REF_ORDER_ID'이면 빠르게 탐색할 수 있습니다.

2-2. 실행 계획 분석
```
-> Limit: 5 row(s)  (actual time=330..330 rows=5 loops=1)
    -> Sort: TOTAL_SOLD_QUANTITY DESC, limit input to 5 row(s) per chunk  (actual time=330..330 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=329..330 rows=999 loops=1)
            -> Aggregate using temporary table  (actual time=329..329 rows=999 loops=1)
                -> Nested loop inner join  (cost=21991 rows=10411) (actual time=2.74..300 rows=10395 loops=1)
                    -> Nested loop inner join  (cost=10616 rows=10411) (actual time=2.71..275 rows=10409 loops=1)
                        -> Filter: ((o.`status` = 'COMPLETED') and (o.ordered_at > <cache>((now() - interval 3 day))))  (cost=638 rows=3150) (actual time=0.0853..5.35 rows=3150 loops=1)
                            -> Covering index range scan on O using idx_order_status_orderedat over (status = 'COMPLETED' AND '2025-02-09 16:24:40.000000' < ordered_at)  (cost=638 rows=3150) (actual time=0.0783..2.08 rows=3150 loops=1)
                        -> Index lookup on OD using idx_orderdetail_orderid_productid (ref_order_id=o.id), with index condition: (od.ref_product_id is not null)  (cost=2.84 rows=3.31) (actual time=0.0755..0.0852 rows=3.3 loops=3150)
                    -> Single-row index lookup on P using PRIMARY (id=od.ref_product_id)  (cost=0.993 rows=1) (actual time=0.00212..0.00216 rows=0.999 loops=10409)

```
- '주문 → 주문상세' 조인 순서로, 먼저 ORDER 테이블을 상태, 날짜 조건으로 필터링한 후, 
  그 결과만큼만 ORDER_DETAIL에서 조회하므로 **불필요한 대량 스캔을 방지** 합니다.  

2-3. 성능 비교    

| 구분                 |    1회   |    2회   |    3회   |    4회   |    5회   |    6회   |    7회   |    8회   |    9회   |   10회   |   평균   | 
|--------------------|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|
| **기존 쿼리 + 인덱스**    |  89.4254 | 144.4217 | 152.8543 | 112.6045 | 143.7800 | 129.0827 | 186.1632 | 151.1021 | 125.9169 | 145.1157 | 138.0466 | 
| **인덱스 없음**         | 62.81 | 79.94  | 63.95  | 59.53  | 68.80  | 69.29  | 67.38  | 63.80  | 66.54  | 70.24  | 67.23  |
| **쿼리 개선 + 인덱스 보강** |   0.5643 |   0.1840 |   0.1705 |   0.1698 |   0.1618 |   0.2272 |   0.1813 |   0.1831 |   0.1736 |   0.1714 |   0.2187 |
- 평균 속도가 약 138.04s 에서 약 0.2187s 으로 약 **99.84%** 개선되었으며, 인덱스를 생성하지 않은 경우보다 **99.67%** 향상된 성능을 보였습니다.

### 5.2. 상품 목록 조회
#### 5.2.1 조회 쿼리
- 상품 목록 조회 쿼리는 다음과 같습니다.
  ```
  SELECT P.PRODUCT_NAME, P.CATEGORY, P.PRICE
  FROM PRODUCT P  
  WHERE P.CATEGORY = ?
  AND P.PRICE BETWEEN ? AND ?  
  AND P.PRODUCT_NAME CONCAT('%', ?, '%')
  LIMIT ? OFFSET ?
  ```
#### 5.2.2. 인덱스 설정
- PRODUCT_NAME에 대한 LIKE 연산은 앞에 와일드카드(%)가 있으면 인덱스 활용이 어려우므로, 이 경우 인덱스 활용이 제한적입니다.
- CATEGORY 와 PRICE 는 동적쿼리로 구성되어 있어 복합 인덱스 생성 시 선두 컬럼이 동작하지 않을 우려가 있습니다. 따라서 단일 인덱스로 생성한 경우와 복합 인덱스를 생성한 경우를 모두 비교해 보았습니다. 

1. 단일 인덱스 2개
   ```
   CREATE INDEX idx_product_category ON PRODUCT(CATEGORY);
   ```
   ```
   CREATE INDEX idx_product_price ON PRODUCT(PRICE);
   ``` 
2. 복합 인덱스 1개
    ```
    CREATE INDEX idx_product_category_price ON PRODUCT(CATEGORY, PRICE);
    ```

#### 5.2.3 성능 비교   
1-1. 테스트 환경  
  - MySql 8.0 컨테이너 2개 띄워, 동일한 양의 데이터 셋팅.
  - 데이터 :
    - PRODUCT - 500만 건
  - 테스트 방법 :
    - 컨테이너 1번 : 인덱스 생성하지 않고 모든 쿼리 테스트 / 복합 인덱스 생성 후 테스트
    - 컨테이너 2번 : 단일 인덱스 생성 후 테스트  
    
1-2. 쿼리별 성능 비교
1. 단일 컬럼 검색 (카테고리)
```
SELECT COUNT(*) 
  FROM PRODUCT 
 WHERE CATEGORY = 'TOP';
```
| 구분         |    1회   |    2회   |    3회   |    4회   |    5회   |    6회   |    7회   |    8회   |    9회   |   10회  |   평균  |
|------------|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|---------:|--------:|--------:|
| **인덱스 없음** | 1.6317   | 1.6842   | 1.6222   | 1.6226   | 1.6212   | 1.6160   | 1.5977   | 1.6226   | 1.6999   | 1.6251  | 1.6343  |
| **단일 인덱스** | 0.2645   | 0.2366   | 0.2341   | 0.2661   | 0.2496   | 0.2413   | 0.2393   | 0.2791   | 0.2396   | 0.2588  | 0.2509  |
| **복합 인덱스** | 0.2515   | 0.2469   | 0.2406   | 0.2334   | 0.2484   | 0.2471   | 0.2548   | 0.2389   | 0.2434   | 0.2959  | 0.2501  |

- 비교 결과
  - 인덱스를 생성한 것이 인덱스를 생성하지 않은 경우보다 성능이 우세합니다.
  - 단일 인덱스와 복합 인덱스의 소요 시간에 큰 차이가 없습니다.

2. 범위 검색 (가격)
```
SELECT COUNT(*) 
  FROM PRODUCT 
 WHERE PRICE BETWEEN 10000 AND 20000;
```
| 구분         |   1회   |   2회   |   3회   |   4회   |   5회   |   6회   |   7회   |   8회   |   9회   |  10회  |  평균   |
|--------------|--------:|--------:|--------:|--------:|--------:|--------:|--------:|--------:|--------:|-------:|--------:|
| **인덱스 없음** | 2.6046 | 2.0811 | 2.1604 | 2.0646 | 2.0290 | 2.1320 | 2.0360 | 2.0750 | 2.0600 | 2.0550 | 2.1298 |
| **단일 인덱스** | 0.0273 | 0.0238 | 0.0317 | 0.0181 | 0.0199 | 0.0305 | 0.0223 | 0.0185 | 0.0203 | 0.0217 | 0.0234 |
| **복합 인덱스** | 0.0281 | 0.0282 | 0.0260 | 0.0235 | 0.0243 | 0.0253 | 0.0364 | 0.0282 | 0.0281 | 0.0388 | 0.0287 |
- 비교 결과
    - 인덱스를 생성한 것이 인덱스를 생성하지 않은 경우보다 성능이 우세합니다.
    - 단일 인덱스와 복합 인덱스의 소요 시간에 큰 차이가 없습니다.

3. 복합 검색 (CATEGORY + PRICE)
```
SELECT ID, PRODUCT_NAME, PRICE
  FROM PRODUCT
 WHERE CATEGORY = 'TOP'
   AND PRICE BETWEEN 10000 AND 50000
ORDER BY PRICE DESC
LIMIT 100 OFFSET 0;
```
| 구분         |   1회   |   2회   |   3회   |   4회   |   5회   |   6회   |   7회   |   8회   |   9회   |  10회  |
|--------------|--------:|--------:|--------:|--------:|--------:|--------:|--------:|--------:|--------:|-------:|
| 인덱스 없음  | 3.0863  | 2.9671  | 3.0660  | 3.2099  | 2.9838  | 3.0296  | 3.1121  | 3.0835  | 2.9911  | 3.1129 |
| 단일 인덱스  | 0.7139  | 0.8242  | 0.8362  | 1.0500  | 0.7220  | 0.8738  | 0.7224  | 0.7032  | 0.7664  | 0.7667 |
| 복합 인덱스  | 2.6994  | 0.1662  | 0.1629  | 0.1657  | 0.1846  | 0.1682  | 0.2834  | 0.1661  | 0.1778  | 0.1828 |

- 비교 결과
    - 인덱스를 생성한 것이 인덱스를 생성하지 않은 경우보다 성능이 우세합니다.
    - 1회차를 제외하고 **복합 인덱스**의 소요 시간이 단일 인덱스의 소요 시간보다 적게 소요되었습니다.
  
4. 복합 검색 (CATEGORY + PRICE + NAME (like))
```
SELECT ID, PRODUCT_NAME, PRICE
  FROM PRODUCT
 WHERE CATEGORY = 'TOP'
   AND PRICE BETWEEN 10000 AND 50000
   AND PRODUCT_NAME LIKE '%123%'
ORDER BY PRICE DESC
LIMIT 100 OFFSET 0;
```
| 구분            |   1회  |   2회  |   3회  |   4회  |   5회  |   6회  |   7회  |   8회  |   9회  |  10회 | 평균   |
|-----------------|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|------:|-------:|
| **인덱스 없음** | 3.6590 | 3.0755 | 3.0572 | 3.2265 | 2.9838 | 3.1090 | 3.1815 | 3.0102 | 3.0279 | 3.1569 | 3.1488 |
| **단일 인덱스** | 0.1898 | 0.0024 | 0.0031 | 0.0027 | 0.0030 | 0.0024 | 0.0034 | 0.0026 | 0.0035 | 0.0024 | 0.0215 |
| **복합 인덱스** | 0.0390 | 0.0012 | 0.0014 | 0.0011 | 0.0011 | 0.0010 | 0.0014 | 0.0011 | 0.0010 | 0.0012 | 0.0050 |
- 비교 결과
    - 인덱스를 생성한 것이 인덱스를 생성하지 않은 경우보다 성능이 우세합니다.
    - **복합 인덱스**의 소요 시간이 단일 인덱스의 소요 시간보다 적게 소요되었습니다.


5. 복합 검색 (PRICE + NAME)
```
SELECT ID, PRODUCT_NAME, PRICE
  FROM PRODUCT
 WHERE PRICE BETWEEN 10000 AND 50000
   AND PRODUCT_NAME LIKE '%123%'
ORDER BY PRICE DESC
LIMIT 100 OFFSET 0;
```
| 구분        |   1회  |   2회  |   3회  |   4회  |   5회  |   6회  |   7회  |   8회  |   9회  |  10회  |
|-------------|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|-------:|
| 인덱스 없음 | 3.9721 | 3.6249 | 3.7320 | 3.8007 | 3.7643 | 3.7333 | 3.8657 | 3.7759 | 3.9046 | 3.7169 |
| 단일 인덱스 | 0.2144 | 0.2780 | 0.3523 | 0.1954 | 0.1929 | 0.1833 | 0.1996 | 0.2048 | 0.1846 | 0.1914 |
| 복합 인덱스 | 3.7172 | 3.5060 | 3.6464 | 3.6649 | 3.5373 | 3.6284 | 3.6719 | 3.7687 | 3.6385 | 3.8298 |
- 비교 결과
  - 선두 컬럼이 WHERE 조건에서 누락될 경우, 복합 인덱스의 성능은 인덱스를 사용하지 않는 경우와 큰 차이가 없습니다.
  - 이 경우, **단일 인덱스**가 복합 인덱스보다 훨씬 우수한 성능을 보입니다. (0.1914 vs. 3.8298, 약 95%)

#### 5.2.4 결론

- **1, 2번**처럼 **단일 조건**을 조회하는 쿼리에서는, 단일 인덱스와 복합 인덱스 간 성능 차이가 크게 드러나지 않았습니다.
- **3, 4번**처럼 **다중 조건**을 조회하는 쿼리에서는 복합 인덱스가 다소 우세했지만, 월등한 격차를 보이진 않았습니다.
- 그러나 **5번**처럼 **복합 인덱스의 선두 컬럼이 조회 조건에 포함되지 않은 경우**, 단일 인덱스가 월등하게 뛰어난 성능을 보였습니다.
- 이에 따라, 선두 컬럼이 조건에서 누락될 가능성을 고려해 **단일 인덱스 컬럼 2개를 생성**하는 방안이 더 적합하다고 판단하였습니다.

### 5.3. 보유 쿠폰 목록 조회
#### 5.3.1. 조회 쿼리
보유 쿠폰 목록 조회 쿼리는 다음과 같습니다.
  ```
  SELECT *
  FROM COUPON C  
  JOIN COUPON_PUBLISH CP ON C.ID = CP.REF_COUPON_ID  
  WHERE CP.USER_ID = ?
  AND CP.STATUS = ?  
  AND SYSDATE BETWEEN CP.VALID_START_DATE AND CP.VALID_END_DATE;
  ```

#### 5.3.2. 인덱스 설정
  ```
  CREATE INDEX idx_coupon_publish_user_status ON COUPON_PUBLISH (USER_ID, STATUS);
  ```
  ```
  CREATE INDEX idx_coupon_publish_full ON COUPON_PUBLISH (USER_ID, STATUS, VALID_START_DATE, VALID_END_DATE);
  ```

- USER_ID와 STATUS는 WHERE 절의 필수 조건이므로 두 가지 컬럼을 선두로 하는 복합 인덱스 구성이 효과적입니다.  
- 날짜 범위 조건을 함께 포함하면 해당 기간 내 데이터를 빠르게 필터링할 수 있습니다.

### 5.4. 잔액 조회 (결제 로직 내)
#### 5.4.1. 조회 쿼리
잔액 조회 쿼리는 다음과 같습니다.
  ```
  SELECT U.BALANCE  
  FROM USERS U  
  WHERE USER_ID = ?;
  ```
#### 5.4.2. 인덱스 설정
- USER의 ID는 기본키(PK)로 조회되므로 기본키 인덱스가 이미 존재하며, 별도의 추가 인덱스 설정은 필요하지 않습니다.

### 5.5. 주문 정보 조회 (결제 로직 내)
#### 5.5.1. 조회 쿼리 
주문 정보 조회 쿼리는 다음과 같습니다.
  ```
  SELECT *
  FROM ORDER O 
  WHERE O.ID = ?;
  ```
#### 5.5.2. 인덱스 설정
- ORDER의 ID는 기본키로 설정되어 있으므로, 기본키 인덱스가 이미 적용되어 있어 별도의 추가 인덱스 설정은 필요하지 않습니다.

### 5.6 재고 수량 조회 (주문 로직 내)
#### 5.6.1 조회 쿼리
```
SELECT PI.INVENTORY
FROM  PRODUCT P
JOIN  PRODUCT_INVENTORY PI ON P.ID = PI.REF_PRODUCT_ID
WHERE P.ID = ?
```
#### 5.6.2. 인덱스 설정
- PRODUCT의 ID는 기본키로 설정되어 있으므로, 기본키 인덱스가 이미 적용되어 있어 별도의 추가 인덱스 설정은 필요하지 않습니다.

---
## 참고 자료

- MSA를 찢어보자 - Saga Pattern: https://velog.io/@joshuara7235/MSA%EB%A5%BC-%EC%B0%8D%EB%A8%B9%ED%95%B4%EB%B3%B4%EC%9E%90.-feat.-Saga-Pattern
- Jojoldu Tistory 포스팅 243: https://jojoldu.tistory.com/243
- Jojoldu Tistory 포스팅 476: https://jojoldu.tistory.com/476
- Ittrue Tistory 포스팅 331: https://ittrue.tistory.com/331
- MySQL Order By Optimization: https://dev.mysql.com/doc/refman/8.4/en/order-by-optimization.html#order-by-index-use