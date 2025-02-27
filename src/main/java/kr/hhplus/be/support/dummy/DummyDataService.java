package kr.hhplus.be.support.dummy;

import kr.hhplus.be.domain.coupon.enumtype.CouponPublishStatus;
import kr.hhplus.be.domain.coupon.enumtype.CouponStatus;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    private static final int BATCH_SIZE = 1000; // 배치 크기 설정
    private static final int USER_CNT = 10000;
    private static final int COUPON_CNT = 1000;
    private static final int COUPON_PUBLISH_CNT = 10000;
    private static final int PRODUCT_CNT = 100000;
    private static final int ORDER_CNT = 1_500_000;
    private static final int ORDER_DETAIL_CNT = 5_000_000;
    private static final int THREAD_CNT = 1;

    Map<Long, BigDecimal> products = new HashMap<>();
    Map<Long, BigDecimal> orderDetails = new HashMap<>();

    public void initializeData() throws InterruptedException, SQLException {
        deleteAllData();
//        inserUsersMultiThreaded();
        inserCouponMultiThreaded();
        inserCouponPublishMultiThreaded();
//        insertProductsMultiThreaded();
//        insertProductInventoryMultiThreaded();
//        insertOrderDetailMultiThreaded();
//        insertOrderMultiThreaded();
    }

    private void deleteAllData() throws SQLException {
        String[] tables = {"product_inventory"}; //, "orders"}; //{"product", "orders", "order_detail"};
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            connection.setAutoCommit(false);
            for (String table : tables) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE " + table)) {
                    preparedStatement.executeUpdate();
                    log.info(table + " truncated");
                }
            }
            connection.commit();
        }
    }

    public void inserUsersMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = USER_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? USER_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertUsers(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting users for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void inserCouponMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = COUPON_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? COUPON_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertCoupon(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting coupon for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void inserCouponPublishMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = COUPON_PUBLISH_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? COUPON_PUBLISH_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertCouponPublish(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting coupon publish for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }


    public void insertProductsMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = PRODUCT_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? PRODUCT_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertProducts(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting products for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void insertProductInventoryMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = PRODUCT_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? PRODUCT_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertProductInventory(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting product_inventory for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void insertOrderDetailMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = ORDER_DETAIL_CNT / THREAD_CNT;

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? ORDER_DETAIL_CNT : (i + 1) * rangePerThread;

            executorService.submit(() -> {
                try {
                    insertOrderDetails(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting order_detail for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void insertOrderMultiThreaded() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT);

        int rangePerThread = ORDER_CNT / THREAD_CNT; // 150,000

        for (int i = 0; i < THREAD_CNT; i++) {
            final long start = i * rangePerThread + 1;
            final long end = (i == THREAD_CNT - 1) ? ORDER_CNT : (i + 1) * rangePerThread + start;

            executorService.submit(() -> {
                try {
                    log.info("start inserting orders for range " + start + " to " + end);
                    insertOrders(start, end);
                } catch (SQLException e) {
                    log.error("Error inserting orders for range " + start + " to " + end, e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    private void insertUsers(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO users (id, username, balance, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setString(2, "User" + i);
                preparedStatement.setBigDecimal(3, getRandomBigDecimal());
                preparedStatement.setString(4, getCurrentDateTime());
                preparedStatement.setString(5, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();

                    totalBatchCounter += batchCounter;
                    log.info("[USERS] 삽입된 데이터 건수: " + totalBatchCounter + "/" + PRODUCT_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private void insertCoupon(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO coupon (id, coupon_name, discount_type, discount_value, max_quantity, remain_quantity, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setString(2, "Coupon" + i);
                preparedStatement.setString(3, getRandomCouponDiscountType(random));
                preparedStatement.setInt(4, getRandomInt(random, 99));
                Integer maxQuantity = getRandomInt(random, 50);
                preparedStatement.setInt(5, maxQuantity);
                preparedStatement.setInt(6, maxQuantity);
                preparedStatement.setString(7, getRandomCouponStatus(random));
                preparedStatement.setString(8, getCurrentDateTime());
                preparedStatement.setString(9, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();

                    totalBatchCounter += batchCounter;
                    log.info("[COUPON] 삽입된 데이터 건수: " + totalBatchCounter + "/" + PRODUCT_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private void insertCouponPublish(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO coupon_publish (id, ref_coupon_id, ref_user_id, publish_date, status, valid_start_date, valid_end_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setLong(2, getRandomLong(random, COUPON_CNT));
                preparedStatement.setLong(3, getRandomLong(random, USER_CNT));
                preparedStatement.setString(4, getCurrentDate());
                preparedStatement.setString(5, getRandomCouponPublishStatus(random));
                preparedStatement.setString(6, getLocalDate(2025, 02, 27));
                preparedStatement.setString(7, getLocalDate(2026, 02, 27));
                preparedStatement.setString(8, getCurrentDateTime());
                preparedStatement.setString(9, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();

                    totalBatchCounter += batchCounter;
                    log.info("[COUPON_PUBLSIH] 삽입된 데이터 건수: " + totalBatchCounter + "/" + PRODUCT_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private void insertProducts(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO product (id, product_name, category, price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {

                BigDecimal price = getRandomBigDecimal();
                products.put(i, price);

                preparedStatement.setLong(1, i);
                preparedStatement.setString(2, "Product " + i);
                preparedStatement.setString(3, getCategory());
                preparedStatement.setBigDecimal(4, price);
                preparedStatement.setString(5, getCurrentDateTime());
                preparedStatement.setString(6, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();

                    totalBatchCounter += batchCounter;
                    log.info("[PRODUCT] 삽입된 데이터 건수: " + totalBatchCounter + "/" + PRODUCT_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private void insertProductInventory(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO product_inventory (ref_product_id, inventory, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {

                BigDecimal price = getRandomBigDecimal();
                products.put(i, price);

                preparedStatement.setLong(1, i);
                preparedStatement.setInt(2, getRandomInt(random, 100));
                preparedStatement.setString(3, getCurrentDateTime());
                preparedStatement.setString(4, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();

                    totalBatchCounter += batchCounter;
                    log.info("[PRODUCT_INVENTORY] 삽입된 데이터 건수: " + totalBatchCounter + "/" + PRODUCT_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private void insertOrderDetails(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO order_detail (id, ref_order_id, ref_product_id, quantity, price, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int batchCounter = 0;
            int totalBatchCounter = 0;
            for (long i = startId; i <= endId; i++) {
                preparedStatement.setLong(1, i); // 고유한 ID 생성

                long orderId = getRandomLong(random, ORDER_CNT);
                preparedStatement.setLong(2, orderId);
                long productId = getRandomLong(random, PRODUCT_CNT);
                int quantity = getRandomInt(random, 1000);
                BigDecimal price = products.get(productId);
                price = (price == null) ? getRandomBigDecimal() : price;
                preparedStatement.setLong(3, productId);
                preparedStatement.setInt(4, quantity);
                preparedStatement.setBigDecimal(5, price);

                BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));
                BigDecimal bfAmount = orderDetails.getOrDefault(orderId, BigDecimal.ZERO);
                orderDetails.put(orderId, bfAmount.add(totalAmount));

                preparedStatement.setBigDecimal(6, totalAmount);
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    totalBatchCounter += batchCounter;
                    log.info("[ORDER_DETAILS] 삽입된 데이터 건수: " + totalBatchCounter + "/" + ORDER_DETAIL_CNT);
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }


    private void insertOrders(long startId, long endId) throws SQLException {
        String sql = "INSERT INTO orders (id, ref_user_id, ref_coupon_publish_id, ordered_at, total_amount, discount_amount"
                + ", final_amount, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            long id = 1;
            int batchCounter = 0;
            int totalBatchCounter = 0;

            log.info("insert orders start: {}, end: {}", startId, endId);
            for (long i = startId; i <= endId; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.setLong(2, getRandomLong(random, USER_CNT));
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, getRandomDateTime(random));
                BigDecimal amount = getRandomBigDecimal();
                preparedStatement.setBigDecimal(5, amount);
                preparedStatement.setBigDecimal(6, BigDecimal.ZERO);
                preparedStatement.setBigDecimal(7, amount);
                preparedStatement.setString(8, getRandomOrderStatus(random));
                preparedStatement.setString(9, getCurrentDateTime());
                preparedStatement.setString(10, getCurrentDateTime());
                preparedStatement.addBatch();

                batchCounter++;
                if (batchCounter % BATCH_SIZE == 0) {
                    totalBatchCounter += batchCounter;
                    log.info("[ORDER] 삽입된 데이터 건수: " + totalBatchCounter + "/" + ORDER_CNT);
                    preparedStatement.executeBatch();
                    connection.commit();
                    batchCounter = 0;
                }
            }

            if (batchCounter > 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getRandomDateTime(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(365);
        long days = startDate.toEpochDay() + random.nextInt(365);
        LocalDate randomDate = LocalDate.ofEpochDay(days);
        LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));
        return LocalDateTime.of(randomDate, randomTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getCategory() {
        Category[] categories = Category.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(categories.length);
        return categories[randomIndex].name();
    }

    private BigDecimal getRandomBigDecimal() {
        int minMultiplier = 1000 / 1000;   // 10
        int maxMultiplier = 990000 / 1000; // 9900

        int randomMultiplier = ThreadLocalRandom.current().nextInt(minMultiplier, maxMultiplier + 1);
        long randomPrice = (long) randomMultiplier * 1000; // 1000의 단위로 값 계산

        // 1000 ~ 990000 사이의 가격 리턴 (1000 단위)
        return BigDecimal.valueOf(randomPrice);
    }

    private Long getRandomLong(Random random, int range) {
         return random.nextLong(range);
    }

    private int getRandomInt(Random random, int range) {
        return random.nextInt(range);
    }

    private String getRandomOrderStatus(Random random) {
        OrderStatus[] orderStatuses = OrderStatus.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(orderStatuses.length);
        return orderStatuses[randomIndex].name();
    }

    private String getRandomCouponDiscountType(Random random) {
        DiscountType[] discountTypes = DiscountType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(discountTypes.length);
        return discountTypes[randomIndex].name();
    }

    private String getRandomCouponStatus(Random random) {
        CouponStatus[] couponStatus = CouponStatus.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(couponStatus.length);
        return couponStatus[randomIndex].name();
    }

    private String getRandomCouponPublishStatus(Random random) {
        CouponPublishStatus[] couponPublishStatuses = CouponPublishStatus.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(couponPublishStatuses.length);
        return couponPublishStatuses[randomIndex].name();
    }
}
