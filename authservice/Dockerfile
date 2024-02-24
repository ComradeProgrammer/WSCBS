FROM openjdk:17-oracle as builder
WORKDIR /app
COPY . . 
RUN  ls && ./mvnw clean package -pl authservice -pl common -DskipTests 

FROM openjdk:17-oracle
WORKDIR /app
COPY --from=builder /app/authservice/target/authservice-0.0.1-SNAPSHOT.jar .
CMD ["java" ,"-jar", "authservice-0.0.1-SNAPSHOT.jar"]