# Sample SQS Application using Spring Boot

This example has everything needed to locally run and test your Amazon SQS application locally in a 
Spring Boot app.  While not exiting, everything is here to:
1. Run SQS locall via [ElasticMq](https://github.com/adamw/elasticmq) and [spring-cloud-aws-messaging](https://github.com/spring-cloud/spring-cloud-aws/tree/master/spring-cloud-aws-messaging).
2. Configure spring-cloud-aws-messaging to connect to the local ElasticMq instance and create a new queue.
3. The /example endpoint accepts a JSON payload which is written to the queue, read frome the queue and
echoed back to the user. 

## Jars
* **sqs-core**:  Core configuration needed to connect to a AWS SQS.
* **sqs-elasticmq-local**:  Spring Boot application that runs elastic ms locally for testing.

## Building/Testing
```bash
./gradlew test
./gradlew build  #Also runs unit tests
```

## Run as a Spring Boot Application
```bash
java -jar  ./api-key-boot/build/libs/api-key-boot-1.0-SNAPSHOT.jar
./gradlew bootRun # As an alternative (--debug, --info, --stacktrace, etc)
```

## Example Api Usage
Send message to the queue to be echoed
```bash
curl localhost:8080/example -H 'Content-Type: application/json' -d '{"id": "id", "message": "message"}'
```
