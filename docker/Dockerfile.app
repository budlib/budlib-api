# Note: run from project root
#
# Make sure budlib-mysql container is running
#
# Create the JAR file
#   $ mvn clean package
#
# Build
#   $ docker build -f docker/Dockerfile.app -t budlib-api:1.0.0 .
#
# Run
#   $ docker run -d -it -p 8080:8080 --network budnetwork --name budlib-api budlib-api:1.0.0

FROM eclipse-temurin:11
LABEL description="RESTful API for BudLib"
WORKDIR /usr/budlib-api
ADD target/api-*.jar /usr/budlib-api/budlib-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.datasource.url=jdbc:mysql://budlib-mysql:3306/buddb", "-jar", "/usr/budlib-api/budlib-api.jar"]
