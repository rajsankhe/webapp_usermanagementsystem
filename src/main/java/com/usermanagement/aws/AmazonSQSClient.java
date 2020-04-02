package com.usermanagement.aws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.usermanagement.services.BillService;

@Service
public class AmazonSQSClient {
	
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
	
	public void sendMessageToSNS() throws JsonProcessingException
	{
		List<Message> queueMessages= receiveMessage();
		String queueUrl = getQueueURL();
		for (Message message : queueMessages) {
			String emailID= message.getBody().split("$")[0];
			String days= message.getBody().split("$")[1];
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
			amazonSNSClient.publishToTopic(mapper.writeValueAsString(snsMessage));
            sqs.deleteMessage(queueUrl, message.getReceiptHandle());
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
