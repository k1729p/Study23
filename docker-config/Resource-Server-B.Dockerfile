FROM maven:3.9.9-eclipse-temurin-24 AS maven_tool
COPY pom.xml /tmp/pom.xml
COPY gateway/pom.xml /tmp/gateway/pom.xml
COPY resource-server-a/pom.xml /tmp/resource-server-a/pom.xml
COPY resource-server-b/pom.xml /tmp/resource-server-b/pom.xml
COPY resource-server-b/src /tmp/resource-server-b/src/
WORKDIR /tmp/
RUN mvn -f pom.xml --projects resource-server-b install -DskipTests

FROM eclipse-temurin:24
COPY --from=maven_tool /tmp/resource-server-b/target/Study23-resource-server-b-1.0.0-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","-jar","application.jar"]