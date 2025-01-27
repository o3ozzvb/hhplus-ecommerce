plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

	/// QueryDSL
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")

	// DB
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("io.netty:netty-resolver-dns-native-macos")

	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.redisson:redisson-spring-boot-starter:3.20.1")

	// Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// redis test
	testImplementation("com.redis:testcontainers-redis:2.2.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}

/**
 * QueryDSL Build Options
 */
val querydslDir = "src/main/generated"

sourceSets {
	getByName("main").java.srcDirs(querydslDir)
}

tasks.withType<JavaCompile> {
	options.generatedSourceOutputDirectory = file(querydslDir)
}

tasks.named("clean") {
	doLast {
		file(querydslDir).deleteRecursively()
	}
}
