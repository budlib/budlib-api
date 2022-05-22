# Note: run from project root
#
# Make sure budlib-mysql container is running
#
# Create the JAR file
#   $ mvn clean package
#
# Build the image
#   $ docker build -f docker/Dockerfile.app -t budlib-api:1.0.0 .
#
# Run the API
#   $ docker run -d -it -p 8080:8080 --network budnetwork --name budlib-api budlib-api:1.0.0


FROM eclipse-temurin:11
LABEL description="RESTful API for BudLib"
CMD ["mkdir", "app"]
ADD target/api-*.jar app/budlib-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.datasource.url=jdbc:mysql://budlib-mysql:3306/buddb", "-jar", "/app/budlib-api.jar"]