package com.usermanagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.passay.AllowedRegexRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	public Bill save(Bill bill){
		return billRepository.save(bill);
	}

	public List<Bill> getBills(String name) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			List<Bill> bills= billRepository.findByOwnerId(loggedUser.getId());
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
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), UUID.fromString(id));
			if(bill!=null)
				return bill;
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public Bill updateBill(String name, String id, Bill bill) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill billOp= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), UUID.fromString(id));
			if(billOp!=null)
			{
				billOp.setVendor(bill.getVendor());
				billOp.setDueDate(bill.getDueDate());
				billOp.setBillDate(bill.getBillDate());
				billOp.setAmountDue(bill.getAmountDue());
				billOp.setCategories(bill.getCategories());
				billOp.setPaymentStatus(bill.getPaymentStatus());
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

	public void deleteBill(String name, String id) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), UUID.fromString(id));
			if(bill!=null)
				billRepository.delete(bill);
			else
				throw new ResourceNotFoundException("Bill does not exist for given id and logged user");
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}

	public File saveAttachment(String name, UUID billId, MultipartFile fileinput) throws ResourceNotFoundException, FileStorageException, ValidationException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), billId);
			if(bill!=null)
			{
				String originalFileName= fileinput.getOriginalFilename();
				String ext= originalFileName.split(".",-1)[1];
				List<String> exts= new ArrayList<String>();
				exts.add("pdf");
				exts.add("png");
				exts.add("jpg");
				exts.add("jpeg");
				if(!exts.contains(ext.toLowerCase()))
				{
					throw new ValidationException("Application supports bill formats such as pdf, png, jpg, and jpeg");
				}
				String fileLocation = fileStorageUtil.storeFile(fileinput);
				File file = new File();
				file.setUrl(fileLocation);
				file.setFileNameStored(commonUtil.getFileNameFromPath(fileLocation));
				file.setContentType(fileinput.getContentType());
				file.setBill(bill);
				file.setUploadDate(commonUtil.getCurrentDateWithFormat("yyyy-MM-dd"));
				file.setOriginalFileName(fileinput.getOriginalFilename());
				file.setSize(fileinput.getSize());
				file.setOwner(loggedUser.getEmailAddress());
				return fileReposiory.save(file);
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
				if(file.getId().compareTo(fileId)==0)
				{
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

	public void deleteFile(String name, UUID billId, UUID fileId) throws ResourceNotFoundException {
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
		if(loggedUser!=null)
		{
			Bill bill= billRepository.findByOwnerIdAndBillId(loggedUser.getId(), billId);
			if(bill!=null)
			{
				File file= bill.getAttachment();
				if(file.getId().compareTo(fileId)==0)
				{
					fileReposiory.delete(file);
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
}
