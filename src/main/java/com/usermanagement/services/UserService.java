package com.usermanagement.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.passay.AllowedRegexRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.timgroup.statsd.StatsDClient;
import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.exceptions.ValidationException;
import com.usermanagement.models.Bill;
import com.usermanagement.models.User;
import com.usermanagement.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StatsDClient statsDClient;	

	private static final Logger logger = LogManager.getLogger(UserService.class);


	public User save(User newUser) throws ValidationException{
		User user= userRepository.findByEmailAddress(newUser.getEmailAddress().toLowerCase());
		if(user == null)
		{
			boolean passwordValid= checkPassword(newUser.getPassword());
			if(!passwordValid)
			{
				throw new ValidationException("Password should be between 8 and 64 character and it should contain at least one lowercase, uppercase, number, and symbol");
			}
			String passwordHash = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
			newUser.setPassword(passwordHash);
			newUser.setEmailAddress(newUser.getEmailAddress().toLowerCase());
			long startTime= System.currentTimeMillis();
			userRepository.save(newUser);
			long endTime= System.currentTimeMillis();
			statsDClient.recordExecutionTime("userSaveQuery", endTime-startTime);
			logger.info("User saved successfully"+newUser);
		}
		else {
			throw new ValidationException("User already exists.");
		}
		return newUser;
	}

	public void updateUser(User updateUser, String userName) throws ValidationException, ResourceNotFoundException {
		User user= userRepository.findByEmailAddress(userName.toLowerCase());
		if(userName.equalsIgnoreCase(updateUser.getEmailAddress()) )
		{
			if(updateUser.getAccountCreated()!=null || updateUser.getAccountUpdated()!=null)
			{
				throw new ValidationException("User can only update First Name, Last Name and Password");
			}
			boolean passwordValid= checkPassword(updateUser.getPassword());
			if(!passwordValid)
			{
				throw new ValidationException("Password should be between 8 and 64 character and it should contain at least one lowercase, uppercase, number, and symbol ");
			}
			String passwordHash = BCrypt.hashpw(updateUser.getPassword(), BCrypt.gensalt());
			user.setPassword(passwordHash);
			user.setFirstName(updateUser.getFirstName());
			user.setLastName(updateUser.getLastName());
			long startTime= System.currentTimeMillis();
			userRepository.save(user);
		 	long endTime= System.currentTimeMillis();
			statsDClient.recordExecutionTime("updateUserQuery", endTime-startTime);
			logger.info("User updated successfully"+user);
		}
		else
		{
			throw new ValidationException("User can only update his account information");
		}
	}


	public User getUser(String name) throws ResourceNotFoundException {
		long startTime= System.currentTimeMillis();
		User loggedUser= userRepository.findByEmailAddress(name.toLowerCase());
	 	long endTime= System.currentTimeMillis();
		statsDClient.recordExecutionTime("getUserQuery", endTime-startTime);
		if(loggedUser!=null)
		{
			logger.info("User retrieved successfully"+loggedUser);
			return loggedUser;
		}
		else
		{
			throw new ResourceNotFoundException("User with email id: " + name + " does not exist");
		}
	}
	
	public boolean checkPassword(String password) {
		//The following regex ensures at least one lowercase, uppercase, number, and symbol exist in a 8+ character length password:
		Rule ruleRegex = new AllowedRegexRule("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,64})"); 
		PasswordValidator passwordValidator = new PasswordValidator(ruleRegex);
		PasswordData passwordData = new PasswordData(password);
		RuleResult validate = passwordValidator.validate(passwordData);
		logger.info("Checking password strength");
		return validate.isValid();
	}

	public boolean auhorizeRequest(String userName, String password) {
		User loggedUser= userRepository.findByEmailAddress(userName.toLowerCase());
		if(loggedUser != null)
		{
			if(BCrypt.checkpw(password, loggedUser.getPassword()))
			{
				return true;
			}
			
		}
		return false;
	}


}
