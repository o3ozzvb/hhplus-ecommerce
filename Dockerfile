# 1. 빌드용 JDK 17 이미지 사용
FROM openjdk:17-jdk-slim AS builder

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Git 및 필수 패키지 설치
RUN apt-get update && apt-get install -y git

# 4. 프로젝트 전체 복사
COPY . /app

# 5. Gradle 실행 권한 부여
RUN chmod +x /app/gradlew

# 6. Gradle 빌드 실행
RUN cd /app && ./gradlew clean build -x test --no-daemon

# 7. 실행을 위한 JDK 17 이미지 사용 (여기서 op → openjdk:17-jdk-slim으로 변경!)
FROM openjdk:17-jdk-slim

# 8. 실행 디렉토리 설정
WORKDIR /app

# 9. 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 10. 컨테이너 실행 시 JAR 실행
CMD ["java", "-jar", "/app/app.jar"]
