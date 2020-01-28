package com.usermanagement.services;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.passay.AllowedRegexRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.exceptions.ValidationException;
import com.usermanagement.models.Bill;
import com.usermanagement.models.User;
import com.usermanagement.repositories.BillRepository;
import com.usermanagement.repositories.UserRepository;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepository;

	@Autowired
	private UserRepository userRepository;
	
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
}
