FROM openjdk:18.0.2.1-slim-buster
WORKDIR /opt/digdes-school-proj
COPY ./app/target/app-0.1.1-SNAPSHOT.jar ./
CMD java -jar app-0.1.1-SNAPSHOT.jar