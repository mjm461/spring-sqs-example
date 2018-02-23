package com.example;

import akka.stream.BindFailedException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.example.model.ExampleObject;
import com.example.service.ExampleObjectEcho;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.junit.Assert.assertEquals;

public class ExampleApplicationTests {

	private static final String QUEUE_NAME = "sqs-queue-name";
	private static final int SQS_PORT = 9324;
	private static final String SQS_HOSTNAME = "localhost";

	private SQSRestServer sqsRestServer;
	private ExampleObjectEcho exampleObjectEcho;
	private QueueMessagingTemplate queueMessagingTemplate;

	@Before
	public void setUp() {

		try {
			sqsRestServer = SQSRestServerBuilder.withPort(SQS_PORT).withInterface(SQS_HOSTNAME).start();
		} catch (BindFailedException e) {
			e.printStackTrace();
		}

		AmazonSQSAsync awsSQSAsyncClient = AmazonSQSAsyncClient.asyncBuilder().withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration("http://" + SQS_HOSTNAME + ":" + SQS_PORT,"eu-west-1")).
				withCredentials(
						new AWSCredentialsProvider(){
							@Override
							public AWSCredentials getCredentials(){
								return new BasicAWSCredentials("acccess", "secret");
							}

							@Override
							public void refresh() {

							}
						}).build();

		awsSQSAsyncClient.createQueue(QUEUE_NAME);

		queueMessagingTemplate = new QueueMessagingTemplate(awsSQSAsyncClient);
		queueMessagingTemplate.setDefaultDestinationName(QUEUE_NAME);

		exampleObjectEcho = new ExampleObjectEcho(queueMessagingTemplate, QUEUE_NAME);

	}
	@After
	public void tearDown() throws Exception {
		if(sqsRestServer != null)
			sqsRestServer.stopAndWait();
	}

	@Test
	public void givenValidPriceChange_whenSendSqsMsg_theVerifyReceivedMsg() throws Exception {
		ExampleObject exampleObject = new ExampleObject();
		exampleObject.setId("id");
		exampleObject.setMessage("message");
		exampleObjectEcho.sendSqsMessage(exampleObject);
		ExampleObject actualResponse = queueMessagingTemplate.receiveAndConvert(QUEUE_NAME,ExampleObject.class);
		assertEquals("message", actualResponse.getMessage());
	}

}
