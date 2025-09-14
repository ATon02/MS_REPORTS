FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S appuser && adduser -S appuser -G appuser
VOLUME /tmp
COPY applications/app-service/build/libs/*.jar  Reports.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Replace with a non-root user to avoid running the container with excessive privileges
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar Reports.jar" ]
