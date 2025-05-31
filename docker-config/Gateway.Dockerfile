FROM maven:3.9.9-eclipse-temurin-24 AS maven_tool
COPY pom.xml /tmp/pom.xml
COPY gateway/pom.xml /tmp/gateway/pom.xml
COPY resource-server-a/pom.xml /tmp/resource-server-a/pom.xml
COPY resource-server-b/pom.xml /tmp/resource-server-b/pom.xml
COPY gateway/src /tmp/gateway/src/
WORKDIR /tmp/
RUN mvn -f pom.xml --projects gateway install -DskipTests

FROM eclipse-temurin:24
RUN apt-get update -qq && apt-get install -y -qq curl && rm -rf /var/lib/apt/lists/*
COPY --from=maven_tool /tmp/gateway/target/Study23-gateway-1.0.0-SNAPSHOT.jar application.jar
ENTRYPOINT ["/bin/sh", "-c", " \
  echo 'Waiting for Keycloak to initialize...'; \
  until curl --silent http://keycloak:8080/realms/spring > /dev/null; do \
    echo 'Keycloak not ready, retrying in 10 seconds...'; \
    sleep 10; \
  done; \
  echo 'Keycloak is ready. Starting the application...'; \
  java --enable-native-access=ALL-UNNAMED -jar application.jar" \
]