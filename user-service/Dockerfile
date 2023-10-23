
FROM gradle:8-jdk17-alpine AS builder


COPY --chown=gradle:gradle . /home/gradle/src


WORKDIR /home/gradle/src

RUN gradle build -x test



FROM openjdk:17-jdk-alpine

RUN mkdir /app


COPY --from=builder /home/gradle/src/build/libs/*.jar /app/comment-service.jar


ENV SPRING_DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5431/user-service
ENV SPRING_DATASOURCE_USERNAME=admin
ENV SPRING_DATASOURCE_PASSWORD=password

# Port
EXPOSE 8082

# Entrypoint for the container
ENTRYPOINT ["java","-jar","/app/user-service.jar"]