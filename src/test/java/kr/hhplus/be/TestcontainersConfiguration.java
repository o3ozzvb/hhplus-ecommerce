package kr.hhplus.be;

import com.redis.testcontainers.RedisContainer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Configuration
class TestcontainersConfiguration {

	private static final int MYSQL_PORT = 3306;
	public static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withExposedPorts(MYSQL_PORT)
			.withUsername("test")
			.withPassword("test");

	private static final int REDIS_PORT = 6379;
	public static final RedisContainer REDIS_CONTAINER = new RedisContainer(
			RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG))
			.withExposedPorts(REDIS_PORT)
			.waitingFor(Wait.forListeningPort());

	private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest")).withKraft();

//	private static final GenericContainer<?> KAFKA_CONTAINER =
//			new GenericContainer<>(DockerImageName.parse("public.ecr.aws/bitnami/kafka:3.5.1"))
//					.withEnv("KAFKA_CFG_NODE_ID", "0")
//					.withEnv("KAFKA_CFG_PROCESS_ROLES", "controller,broker")
//					.withEnv("KAFKA_CFG_LISTENERS", "PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094")
//					.withEnv("KAFKA_CFG_ADVERTISED_LISTENERS", "PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094")
//					.withEnv("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT")
//					.withEnv("KAFKA_CFG_CONTROLLER_QUORUM_VOTERS", "0@127.0.0.1:9093")
//					.withEnv("KAFKA_CFG_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
//					.withExposedPorts(9092, 9093, 9094);


	static {
		MYSQL_CONTAINER.start();
		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

		REDIS_CONTAINER.start();
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());

		KAFKA_CONTAINER.start();
		System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());
//		System.setProperty("spring.kafka.bootstrap-servers", getBootstrapServers());
//		System.setProperty("spring.kafka.consumer.bootstrap-servers", getBootstrapServers());
//		System.setProperty("spring.kafka.producer.bootstrap-servers", getBootstrapServers());
	}

	public static String getBootstrapServers() {
		return KAFKA_CONTAINER.getHost() + ":" + KAFKA_CONTAINER.getMappedPort(9094);
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		// mysql
		registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);

		// redis
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());

		// kafka
		registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
//		registry.add("spring.kafka.bootstrap-servers", TestcontainersConfiguration::getBootstrapServers);
//		registry.add("spring.kafka.consumer.bootstrap-servers", TestcontainersConfiguration::getBootstrapServers);
//		registry.add("spring.kafka.producer.bootstrap-servers", TestcontainersConfiguration::getBootstrapServers);
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
		if (KAFKA_CONTAINER.isRunning()) {
			KAFKA_CONTAINER.stop();
		}
	}
}