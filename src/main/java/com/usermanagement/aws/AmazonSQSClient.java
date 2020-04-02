package com.usermanagement.aws;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.model.Message;

/**
 * Reference
 * https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html
 * @author Raj Sankhe
 *
 */
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class AmazonSQSClient {
	
	@Value("${sqs.queueName}")
	private String sqsBillDueQueue;
	
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	
	public void publishMessage(String demo){
		String queueUrl = getQueueURL();

		SendMessageRequest request = new SendMessageRequest()
		        .withQueueUrl(queueUrl)
		        .withMessageBody(demo)
		        .withDelaySeconds(5);
		sqs.sendMessage(request);
	}

	public String getQueueURL() {
		String queueUrl = sqs.getQueueUrl(sqsBillDueQueue).getQueueUrl();
		return queueUrl;
	}
	
	public List<Message> receiveMessage()
	{
		String queueUrl = getQueueURL();
		return sqs.receiveMessage(queueUrl).getMessages();
	}
	
	public void deleteMessage()
	{
		List<Message> queueMessages= receiveMessage();
		String queueUrl = getQueueURL();
		for (Message message : queueMessages) {
            sqs.deleteMessage(queueUrl, message.getReceiptHandle());
        }
	}
}
