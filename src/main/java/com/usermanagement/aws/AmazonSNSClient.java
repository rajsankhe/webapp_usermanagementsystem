package com.usermanagement.aws;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Service
public class AmazonSNSClient {
	
	private static final Logger logger = LoggerFactory.getLogger(AmazonSNSClient.class);
	private AmazonSNS amazonSNSClient=AmazonSNSClientBuilder.defaultClient();

	@Value("${sns.topicName}")
	private String snsTopicARN;

	public String publishToTopic(String msg) {
		logger.debug("publish message to topic");
		PublishRequest publishRequest = new PublishRequest(snsTopicARN, msg);
		PublishResult publishResponse = amazonSNSClient.publish(publishRequest);
		logger.debug("published");
		return publishResponse.getMessageId();

	}

}
