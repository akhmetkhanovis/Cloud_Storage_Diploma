FROM openjdk:16-jdk-alpine
EXPOSE 8080
ADD target/Cloud_Storage_Diploma-0.0.1-SNAPSHOT.jar csapp.jar
ENTRYPOINT ["java", "-jar", "/csapp.jar"]