FROM openjdk:21-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew clean bootJar

FROM openjdk:21-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/Hanmo-0.0.1-SNAPSHOT.jar /tmp/Hanmo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /tmp/Hanmo-0.0.1-SNAPSHOT.jar"]