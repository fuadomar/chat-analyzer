FROM java:openjdk-8u91-jdk
MAINTAINER mozmmal@gmail.com
EXPOSE 8080

ADD tone-analyzer-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]