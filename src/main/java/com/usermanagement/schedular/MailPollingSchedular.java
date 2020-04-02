package com.usermanagement.schedular;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.usermanagement.aws.AmazonSNSClient;
import com.usermanagement.aws.AmazonSQSClient;
import com.usermanagement.services.BillService;

@Component
public class MailPollingSchedular {
	private static final Logger logger = LoggerFactory.getLogger(MailPollingSchedular.class);
	
	@Autowired
	private BillService billService;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient;
	
	@Autowired
	private AmazonSNSClient amazonSNSClient;
	
	@Scheduled(cron = "* * * * * ?")
	public void scheduleTask() {
		
		try {
			amazonSQSClient.sendMessageToSNS();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	}
}
