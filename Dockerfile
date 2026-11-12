# Étape 1 : compilation avec Maven + JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

# Étape 2 : exécution avec un JRE 17 léger
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/clinique-medicale-1.0.0.jar app.jar
EXPOSE 8080
# Le port est fourni par l'hébergeur via PORT (8080 par défaut)
CMD ["sh", "-c", "java -XX:MaxRAMPercentage=75 -Dserver.port=${PORT:-8080} -jar app.jar"]
