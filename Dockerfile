# FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift <--- How is Siob
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]