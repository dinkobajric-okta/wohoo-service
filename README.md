# Wohoo Service

This repo contains a simple web service built on top of Spring Boot Framework that exposes an API through which you can push messages to a Kafka cluster. The integration with Kafka is done through Spring Cloud Stream framework.

## Getting Started

### 1. Start Kafka cluster and Kafka UI
```bash
docker-compose up -d
```

### 2. Verify Kafka is up and running
* Navigating to http://localhost:18081/
  * You may need to wait few seconds until the UI loads.
* When the cluster is up and running, the `Online` tile on the dashboard will show `1 clusters`, and `Offline` tile will show `0 clusters`.

### 3. Build
```bash
./mvnw clean package
```

### 4. Run Service
```bash
java -jar target/wohoo-1.0-SNAPSHOT.jar
```

### 5. Post a message to the service
```bash
curl -v -X POST -H "Content-type: text/plain" -d "let it snow" http://localhost:18080/api/v1/publish
```

### 6. View messages posted to Kafka cluster
* View messages through Kafka UI: http://localhost:18081/ui/clusters/local/all-topics
* You may need to wait 30 seconds or more until the appropriate topics have been created in Kafka and are available for browsing through Kafka UI.

### 7. View Kafka metrics via Actuator endpoint
* Go to http://localhost:18080/actuator/metrics to see various metrics, including Kafka producer metrics.
