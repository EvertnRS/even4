FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libgl1 \
    libx11-xcb1 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6 \
    libasound2t64 \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/even4-21.jar app.jar

CMD ["java", "-jar", "app.jar"]