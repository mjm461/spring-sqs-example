package com.example;

import akka.stream.BindFailedException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.example.service.SqsSendService;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class SqsServiceTests {
	public static class SqsObject{
		private String id;
		public String getId() { return id; }
		public void setId(String id) { this.id = id; }
	}

	private static final String QUEUE_NAME = "sqs-queue-name";
	private static final int SQS_PORT = 9324;
	private static final String SQS_HOSTNAME = "localhost";

	private SQSRestServer sqsRestServer;
	private SqsSendService sqsSendService;
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

		sqsSendService = new SqsSendService(queueMessagingTemplate);

	}

	@After
	public void tearDown(){
		if(sqsRestServer != null)
			sqsRestServer.stopAndWait();
	}

	@Test
	public void givenValidPriceChange_whenSendSqsMsg_theVerifyReceivedMsg() throws Exception {
		SqsObject sqsObject = new SqsObject();
		sqsObject.setId("id");
		sqsSendService.sendSqsMessage(QUEUE_NAME, sqsObject);
		SqsObject actualResponse = queueMessagingTemplate.receiveAndConvert(QUEUE_NAME,SqsObject.class);
		Assert.assertEquals("id", actualResponse.getId());
	}

}
