FROM openjdk:17
EXPOSE 9090
WORKDIR /usr/app
COPY ./target/NFTRAFFLE-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","NFTRAFFLE-1.0-SNAPSHOT.jar"]