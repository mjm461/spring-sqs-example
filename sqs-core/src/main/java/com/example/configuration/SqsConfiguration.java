package com.example.configuration;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.*;


@Configuration
@ComponentScan("com.example")
public class SqsConfiguration {

    @Value("${com.sqsexample.queue.name}") private  String queueName;

    @Bean
    public QueueBufferConfig queueBufferConfig(
            @Value("${com.sqsexample.queue.batch.ms}") Long maxBatchOpenMs,
            @Value("${com.sqsexample.queue.batch.size}") Integer maxBatchSize,
            @Value("${com.sqsexample.queue.batch.outbound}")Integer maxInflightOutboundBatches) {

        return new QueueBufferConfig().
                withMaxBatchOpenMs(maxBatchOpenMs).
                withMaxBatchSize(maxBatchSize).
                withMaxInflightOutboundBatches(maxInflightOutboundBatches);

    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSqsLocal) {

        QueueMessagingTemplate queueMessagingTemplate;
        queueMessagingTemplate = new QueueMessagingTemplate(amazonSqsLocal);
        queueMessagingTemplate.setDefaultDestinationName(queueName);

        return queueMessagingTemplate;

    }

}

