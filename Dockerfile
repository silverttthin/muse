###############################################################
# Stage 1: Builder (Java 21 컴파일 환경)
###############################################################
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# 1. Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle .

# 2. 소스 코드도 여기서 복사 (!!! 수정된 위치 !!!)
# 컴파일 및 JAR 생성을 위해 이 파일들은 반드시 필요합니다.
COPY src ./src

# 3. 권한 부여 및 빌드 (의존성 다운로드, 컴파일, JAR 생성 통합)
RUN chmod +x ./gradlew

# JAR 파일을 생성합니다. (이제 모든 파일이 있으므로 정상 작동합니다)
RUN ./gradlew clean bootJar -x test --parallel --no-daemon


###############################################################
# Stage 2: Runner (가벼운 실행 환경)
###############################################################
# 이전 단계에서 에러가 났던 이미지를 Docker Hub 접근이 용이한 Temurin JRE로 교체합니다.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Stage 1에서 만들어진 .jar 파일만 복사 (멀티 스테이지의 핵심)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-Djava.security.egd=file:/dev/urandom", "-jar", "app.jar"]
