package com.usermanagement.aws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sns.message.SnsMessage;
/**
 * Reference
 * https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html
 * @author Raj Sankhe
 *
 */
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.models.Bill;
import com.usermanagement.models.SNSMessage;
import com.usermanagement.models.User;
import com.usermanagement.repositories.BillRepository;
import com.usermanagement.repositories.UserRepository;
import com.usermanagement.schedular.MailPollingSchedular;
import com.usermanagement.services.BillService;

@Service
public class AmazonSQSClient {
	private static final Logger logger = LoggerFactory.getLogger(AmazonSQSClient.class);
	
	@Value("${sqs.queueName}")
	private String sqsBillDueQueue;
	
	@Value("${domain}")
	private String domain;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BillRepository billRepository;
	
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	
	@Autowired
	private AmazonSNSClient amazonSNSClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	public void publishMessage(String demo){
		String queueUrl = getQueueURL();

		SendMessageRequest request = new SendMessageRequest()
		        .withQueueUrl(queueUrl)
		        .withMessageBody(demo);
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
	
	public void sendMessageToSNS() throws JsonProcessingException
	{
		logger.debug("in sendMessageToSNS ");
		List<Message> queueMessages= receiveMessage();
		String queueUrl = getQueueURL();
		for (Message message : queueMessages) {
			String msg= message.getBody();
			String emailID= msg.split("\\$")[0];
			String days= msg.split("\\$")[1];
			User loggedUser= userRepository.findByEmailAddress(emailID.toLowerCase());
			List<Bill> bills= billRepository.findByOwnerId(loggedUser.getId());
			List<Bill> dueBills= getBillsDueInDays(bills,Integer.parseInt(days));
			List<String> dueBillsLinks= new ArrayList<String>();
			for(Bill bill:dueBills)
			{
				String dueBillLink= "http://"+domain+":80/v1/bill/"+bill.getBillId();
				dueBillsLinks.add(dueBillLink);
			}
			SNSMessage snsMessage= new SNSMessage();
			snsMessage.setEmailId(emailID);
			snsMessage.setDueBills(dueBillsLinks);
			logger.debug("publish message to topic");
			amazonSNSClient.publishToTopic(mapper.writeValueAsString(snsMessage));
			logger.debug("message published");
            sqs.deleteMessage(queueUrl, message.getReceiptHandle());
            logger.debug("message deleted");
        }
	}
	
	public List<Bill> getBillsDueInDays(List<Bill> bills, int days) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
        System.out.println(dateFormat.format(currentDate));

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // add days to current
        c.add(Calendar.DATE, days); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        
        // convert calendar to date
        Date currentDatePlusDays= c.getTime();
        
        List<Bill> dueBills= new ArrayList<Bill>();
        for(Bill bill : bills)
        {
			Date billDate= bill.getDueDate();
        	if(billDate.compareTo(currentDate)==0 || billDate.after(currentDate) && billDate.before(currentDatePlusDays))
        		dueBills.add(bill);
        }
		return dueBills;
	}
}
