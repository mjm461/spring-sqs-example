package com.example.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@Profile({"default", "local"})
public class ExampleConfiguration {

    protected @Value("${com.sqsexample.queue.uri}") String uri;

    @DependsOn("sqsRestServer")
    @Bean
    public AmazonSQSAsync amazonSQS(QueueBufferConfig queueBufferConfig,
                                    @Value("${cloud.aws.region.static}") String region,
                                    @Value("${com.sqsexample.accesskey}") String accessKey,
                                    @Value("${com.sqsexample.secretkey}") String secretKey,
                                    @Value("${com.sqsexample.queue.name}") String queueName) {

        AmazonSQSAsync awsSQSAsyncClient = AmazonSQSAsyncClient.asyncBuilder().
                withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(uri, region)).
                withCredentials(
                    new AWSCredentialsProvider(){
                        @Override
                        public AWSCredentials getCredentials(){
                            return new BasicAWSCredentials(accessKey, secretKey);
                        }

                        @Override
                        public void refresh() {

                        }
                    }).build();

        awsSQSAsyncClient.createQueue(queueName);

        return new AmazonSQSBufferedAsyncClient(awsSQSAsyncClient,queueBufferConfig);

    }

    @Bean
    public SQSRestServer sqsRestServer() {

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();

        SQSRestServer sqsRestServer = SQSRestServerBuilder
                .withPort(Integer.valueOf(uriComponents.getPort()))
                .withInterface(uriComponents.getHost())
                .start();
        return sqsRestServer;

    }

}
