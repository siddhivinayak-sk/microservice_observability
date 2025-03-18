# Kafka on Windows
## Prerequsite for Kafka
- OS - Windows or Linux
- Java 11 (perfer JDK)

Note: Based on OS, Kafka provides sh/bat file in bin directory.

## Setup Kafka
1. Download Kafka from https://kafka.apache.org/downloads
2. Extract the zip in directory c:\kafka. Make sure the name of directory must be 'kafka'
3. Need to start zookeeper by running below command
   ```
     zookeeper-server-start.bat ..\..\config\zookeeper.properties
   ```
   where zookeeper.properties file is available in /config directory of kafka home directory
   once zookeeper started, proceed for next step
4. Need to start Kafka by running below command
   ```
     kafka-server-start.bat ..\..\config\server.properties
   ```
   where server.properties file is available in /config directory of kafka home directory
   once zookeeper started, proceed for next step
5. Download kafdrop from URL: https://github.com/obsidiandynamics/kafdrop
6. Need to start Kafdrop - A user interface for Kafka broker, run below command
   ```
     java -Djdk.io.File.enableADS=true --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -jar kafdrop-3.30.0.jar --kafka.brokerConnect=localhost:9092
   ```
7. By default, Kafka broker will start on 9092 port and kafdrop on 9000 port.
8. Need to start kafdrop in browser by using URL: http://localhost:9000/ it will open kafdrop

## Bat file to start Kafka stack
Create a file named 'start-kafka.bat' with below content:
```
start cmd /k zookeeper-server-start.bat ..\..\config\zookeeper.properties
timeout 20
start cmd /k kafka-server-start.bat ..\..\config\server.properties
timeout 20
java -Djdk.io.File.enableADS=true --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -jar kafdrop-3.30.0.jar --kafka.brokerConnect=localhost:9092
```

## For Testing
### 1. Create a topic
Can open kafdrop by opening URL: http://localhost:9000 and click on create topic button. Provide the name for topic and other details (can keep other detail as default). It will create a topic with specified name.

### 2. Start Kafka console producer
There is a batch/sh file in kafka/bin directory to start kafka-console-producer. Run below command to start producer:
```
kafka-console-producer.bat --topic test --bootstrap-server localhost:9092
```
Where --topic specifies topic name and --bootstarp-server specifies the kafka broker instance.

### 3. Start Kafka console consumer
There is a batch/sh file in kafka/bin directory to start kafka-console-consumer. Run below command to start producer:
```
kafka-console-consumer.bat  --topic test --bootstrap-server localhost:9092
```
Where --topic specifies topic name and --bootstarp-server specifies the kafka broker instance.


### 4. Send message & verify on consumer
Now all setup completed. Type a message 'hello' on kafka-console-producer. It will start reflecting into kafka-console-consumer.
The messages can be discovered from kafdrop web UI as well.


# Kafka in Docker

Run command:

```shell
docker run -d --name=kafka -p 9092:9092 apache/kafka
```

#### References
- https://docs.docker.com/guides/kafka/
- https://spring.io/blog/2022/10/12/observability-with-spring-boot-3
- https://spring.io/blog/2024/10/28/lets-use-opentelemetry-with-spring