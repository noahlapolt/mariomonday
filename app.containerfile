FROM eclipse-temurin:17
ARG JAR_FILE=backendserver/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]