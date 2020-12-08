# Using the Base OpenJDK 11 Image
FROM openjdk:11.0.9.1-jdk

# Set default environment variables here
ENV MYSQL_HOST="mysql-external.mysql56.svc.cluster.local"
ENV MYSQL_PORT="3306"
ENV MYSQL_USER="application"
ENV MYSQL_PASS="db3k4Cc"
ENV MYSQL_DATABASE="ecommerce"

ENV MIN_ORDER_INTERVAL_SECONDS="1"
ENV MAX_ORDER_INTERVAL_SECONDS="5"

ENV SHIPMENT_INTERVAL_SECONDS="5"
ENV SHIPMENT_COUNT="10"

ENV ORDER_RETURN_INTERVAL_SECONDS="2"
ENV ORDER_RETURN_AGE_SECONDS="60"
ENV ORDER_RETURN_COUNT="64"

ENV REPLENISHMENT_INTERVAL_SECONDS="10"

# One of: OrderGeneratorService, OrderShipmentService, OrderDeliveryService, OrderReturnsService
# InventoryReplenishmentService, DefaultService
ENV SERVICE_NAME="DefaultService"

#RUN addgroup spring

#RUN adduser spring

#USER spring:spring

ARG JAR_FILE=target/data-generator-1.0.0-uber.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

# Command to build and push the new Docker image to the remote repository
# docker build . -t izzyacademy/data-generators:1.0
# docker push izzyacademy/data-generators:1.0


# docker stop data-generators
# docker rm data-generators
# docker run --env-file ./env.list --name data-generators izzyacademy/data-generators:1.0