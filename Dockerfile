FROM openjdk:18.0.2.1-slim-buster
WORKDIR /opt/digdes-school-proj
COPY ./app/target/app-1.0-SNAPSHOT.jar ./
CMD java -jar app-1.0-SNAPSHOT.jar