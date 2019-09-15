FROM openjdk:8-jdk-alpine
ARG JAR_FILE
ADD ${JAR_FILE} storage.jar
ENTRYPOINT ["java","-jar","/storage.jar"]