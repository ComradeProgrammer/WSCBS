FROM openjdk:17-oracle as builder
WORKDIR /app
COPY . . 
RUN  ls && ./mvnw clean package -pl urlservice -pl common -DskipTests 

FROM openjdk:17-oracle
WORKDIR /app
COPY --from=builder /app/urlservice/target/urlservice-0.0.1-SNAPSHOT.jar .
CMD ["java" ,"-jar", "urlservice-0.0.1-SNAPSHOT.jar"]