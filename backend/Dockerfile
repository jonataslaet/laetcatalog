FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ADD ./target/laetcatalog-0.0.1-SNAPSHOT.jar laetcatalog.jar
ENTRYPOINT ["java","-jar","/laetcatalog.jar"]