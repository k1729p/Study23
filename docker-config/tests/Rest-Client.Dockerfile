FROM eclipse-temurin:24
RUN apt-get update -qq && apt-get install -y -qq curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY docker-config/tests/call_endpoints.sh call_endpoints.sh
COPY docker-config/tests/Extractor.java Extractor.java
RUN chmod +x call_endpoints.sh
ENTRYPOINT ["/bin/bash", "-c", "./call_endpoints.sh"]