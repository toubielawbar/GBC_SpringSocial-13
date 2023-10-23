
#-----------
# BUILD stage
#--
# base image
FROM gradle:8.4.0-jdk17-alpine AS builder

# copy the application file from the host machine to the image file system
COPY --chown=gradle:gradle . /home/gradle/src/

# set the directory within the image, for executing future commad
WORKDIR /home/gradle/src/

# run gradle build within the container - Not the host machine
RUN gradle build -x test

FROM openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/product-service.jar

ENV MONGO_DB_USERNAME=rootadmin MONGO_DB_PASSWORD=password
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app/product-service.jar"]
