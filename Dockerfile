FROM openjdk:16
ADD target/cloudstorage-0.0.1-SNAPSHOT.jar cloudstorage.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/cloudstorage.jar"]