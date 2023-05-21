FROM maven:3.9.1-amazoncorretto-17 AS builder

COPY src /usr/app/src
COPY .mvn/ .mvn

RUN mvn -f /usr/app/pom.xml -DskipTests clean package && cp /usr/app/target/usermanager-*.jar /docker-image.jar

FROM amazoncorretto:17-alpine-jdk
COPY --from=builder /docker-image.jar /docker-image.jar
RUN sh -c 'touch /docker-image.jar' && apk update
ENTRYPOINT [ "sh", "-c", "java -jar /docker-image.jar" ]
