package com.usermanagement.aws;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Service
public class AmazonSNSClient {

	private AmazonSNS amazonSNSClient;

	@Value("${sns.topicName}")
	private String snsTopicARN;

	@PostConstruct
	private void initializeAmazon() {
		this.amazonSNSClient =AmazonSNSClientBuilder.defaultClient();
	}

	public String publishToTopic(String msg) {
		PublishRequest publishRequest = new PublishRequest(snsTopicARN, msg);
		PublishResult publishResponse = amazonSNSClient.publish(publishRequest);
		return publishResponse.getMessageId();

	}

}
