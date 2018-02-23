package com.example.configuration;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AwsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonSQSAsync amazonSQS(QueueBufferConfig queueBufferConfig) {

        AmazonSQSAsync awsSQSAsyncClient = AmazonSQSAsyncClient.asyncBuilder().
                withCredentials(new InstanceProfileCredentialsProvider(true)).build();
        return new AmazonSQSBufferedAsyncClient(awsSQSAsyncClient,queueBufferConfig);

    }

}
