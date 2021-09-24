FROM adoptopenjdk:11-jdk-hotspot

# add local user and app directory
RUN groupadd -r raappuser && useradd --no-log-init -m -r -u 1001 -g raappuser raappuser && mkdir /app

# copy jar file and entrypoint shell
WORKDIR /app
COPY ./build/libs/reactive-app-0.1.0.jar ./app.jar

# Set file permissions
RUN chown -R raappuser:raappuser .
USER raappuser

CMD java -jar app.jar
