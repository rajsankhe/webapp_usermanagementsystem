package com.usermanagement.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.timgroup.statsd.StatsDClient;
import com.usermanagement.aws.AmazonSNSClient;
import com.usermanagement.aws.AmazonSQSClient;
import com.usermanagement.exceptions.FileStorageException;
import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.exceptions.ValidationException;
import com.usermanagement.models.Bill;
import com.usermanagement.models.File;
import com.usermanagement.models.User;
import com.usermanagement.repositories.BillRepository;
import com.usermanagement.repositories.FileReposiory;
import com.usermanagement.repositories.UserRepository;
import com.usermanagement.util.CommonUtil;
import com.usermanagement.util.file.storage.FileStorageUtil;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileReposiory fileReposiory;
	
	@Autowired
	private FileStorageUtil fileStorageUtil;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private StatsDClient statsDClient;
	
	@Autowired
	private AmazonSQSClient amazonSQSClient;
	
	@Autowired
	private AmazonSNSClient amazonSNSClient;
	
	private static final Logger logger = LogManager.getLogger(BillService.class);
	
	public Bill save(Bill bill) throws ValidationException{
		if(Double.compare(bill.getAmountDue(), 0.01)<0){
			throw new ValidationException("amount_due should be atleast 0.01");
		}
		logger.info("Bill saved successfully");
		long startTime= System.currentTimeMillis();
		Bill result= billRepository.save(bill);
		long endTime= System.currentTimeMillis();
		statsDClient.recordExecutionTime("saveBillQuery", endTime-startTime);
		return result;
	}

	public List<Bill> getBills(String name) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			long startTime= System.currentTimeMillis();
			List<Bill> bills= billRepository.findByOwnerId(loggedUser.getId());
			long endTime= System.currentTimeMillis();
			statsDClient.recordExecutionTime("getBillQuery", endTime-startTime);
			logger.info("Bills retrieved successfully");
			return bills;
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public Bill getBill(String name, String id) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			long startTime= System.currentTimeMillis();
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), UUID.fromString(id));
			long endTime= System.currentTimeMillis();
			statsDClient.recordExecutionTime("getBillQuery", endTime-startTime);
			if(bill!=null)
			{
				logger.info("Bill retrieved successfully");
				return bill;
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public Bill updateBill(String name, String id, Bill bill) throws ResourceNotFoundException, ValidationException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill billOp= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), UUID.fromString(id));
			if(Double.compare(bill.getAmountDue(), 0.01)<0){
				throw new ValidationException("Bill amount due should be atleast 0.01");
			}
			if(billOp!=null)
			{
				billOp.setVendor(bill.getVendor());
				billOp.setDueDate(bill.getDueDate());
				billOp.setBillDate(bill.getBillDate());
				billOp.setAmountDue(bill.getAmountDue());
				billOp.setCategories(bill.getCategories());
				billOp.setPaymentStatus(bill.getPaymentStatus());
				logger.info("Bill updated successfully");
				return billRepository.save(billOp);
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public void deleteBill(String name, UUID id) throws ResourceNotFoundException, FileStorageException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), id);
			if(bill!=null)
			{
				if(bill.getAttachment()!=null)
					deleteFile(name,id,bill.getAttachment().getId());
				logger.info("Bill deleted successfully");
				long startTime= System.currentTimeMillis();
				billRepository.delete(bill);
				long endTime= System.currentTimeMillis();
				statsDClient.recordExecutionTime("deleteBillQuery", endTime-startTime);
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public File saveAttachment(String name, UUID billId, MultipartFile fileinput) throws ResourceNotFoundException, FileStorageException, ValidationException, NoSuchAlgorithmException, IOException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), billId);
			if(bill!=null)
			{
				if(bill.getAttachment()==null)
				{
					String originalFileName= fileinput.getOriginalFilename();
					if(!commonUtil.validateAttachmentExtension(originalFileName))
					{
						throw new ValidationException("Application supports bill formats such as pdf, png, jpg, and jpeg");
					}
					else
					{
						long startTime= System.currentTimeMillis();
						String fileLocation = fileStorageUtil.storeFile(fileinput);
						long endTime= System.currentTimeMillis();
						statsDClient.recordExecutionTime("addFileS3", endTime-startTime);
						File file = new File();
						file.setUrl(fileLocation);
						file.setFileNameStored(commonUtil.getFileNameFromPath(fileLocation));
						file.setContentType(fileinput.getContentType());
						file.setBill(bill);
						file.setUploadDate(commonUtil.getCurrentDateWithFormat("yyyy-MM-dd"));
						file.setOriginalFileName(fileinput.getOriginalFilename());
						file.setSize(fileinput.getSize());
						file.setOwner(loggedUser.getEmailAddress());
						file.setHash(commonUtil.computeMD5Hash(fileinput.getBytes()));
						logger.info("file saved successfully");
						return fileReposiory.save(file);
					}
				}
				else
					throw new ValidationException("Updating existing image requires deleting it first and then uploading a new image.");
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
		
	}

	public File getFile(String name, UUID billId, UUID fileId) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), billId);
			if(bill!=null)
			{
				File file= bill.getAttachment();
				if(file!=null && file.getId().compareTo(fileId)==0)
				{
					logger.info("File retrieved successfully");
					return file;
				}
				else
					throw new ResourceNotFoundException("File with given Id does not exist for bill");
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public void deleteFile(String name, UUID billId, UUID fileId) throws ResourceNotFoundException, FileStorageException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), billId);
			if(bill!=null)
			{
				File file= bill.getAttachment();
				if(file!=null &&file.getId().compareTo(fileId)==0)
				{
					long startTime= System.currentTimeMillis();
					bill.setAttachment(null);
					billRepository.save(bill);
					logger.info("File deleted from bill");
					fileStorageUtil.deleteFile(file.getUrl());
					logger.info("File deleted from storage");
					fileReposiory.delete(file);
					logger.info("File deleted from database");
				 	long endTime= System.currentTimeMillis();
					statsDClient.recordExecutionTime("deleteFileQuery", endTime-startTime);
				}
				else
					throw new ResourceNotFoundException("File with given Id does not exist for bill");
			}
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public List<Bill> getDueBillsInNextDays(String name, int days) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			long startTime= System.currentTimeMillis();
			List<Bill> bills= billRepository.findByOwnerId(loggedUser.getId());
			long endTime= System.currentTimeMillis();
			amazonSQSClient.publishMessage("Hello Raj GM");
			statsDClient.recordExecutionTime("getBillQuery", endTime-startTime);
			logger.info("Bills due are retrieved successfully");
			return getBillsDueInDays(bills,days);
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	private List<Bill> getBillsDueInDays(List<Bill> bills, int days) {
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
