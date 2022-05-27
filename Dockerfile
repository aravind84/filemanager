FROM gcr.io/distroless/java:8
ARG appVersion=1.0
COPY build/libs/filemanager-${appVersion}.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080