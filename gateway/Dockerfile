FROM amazoncorretto:17
COPY target/*.jar app.jar
CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]