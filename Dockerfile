FROM gradle:jdk11 as builder
WORKDIR /app
COPY . /app
RUN gradle --no-daemon --parallel clean build

FROM eclipse-temurin:11-jre-alpine

# add local user and app directory
RUN addgroup -S raappuser && adduser -S raappuser -G raappuser && mkdir /app

# Create and set work directory
WORKDIR /app
# switch user to raappuser
USER raappuser
# Copy application from gradle builder
COPY --chown=appuser:appuser --from=builder /app/build/libs/reactive-app-0.1.0.jar /app/application.jar
# specify the port which will be exposed
EXPOSE 8080
# run application
CMD ["java", "-jar", "application.jar"]
