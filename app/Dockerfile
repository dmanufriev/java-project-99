FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src
COPY config config

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS "-Xmx512M -Xms512M"
EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev