package com.usermanagement.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.models.User;
import com.usermanagement.repositories.UserRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserManagementServiceTest {
	
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void TestGetUser() throws ResourceNotFoundException
	{
		User user= new User();
		user.setFirstName("TestUser");
		user.setLastName("Sankhe");
		user.setEmailAddress("test@gmail.com");
		user.setAccountCreated(LocalDateTime.now());
		user.setAccountUpdated(LocalDateTime.now());
		user.setPassword("test@123");
		userRepository.save(user);	
		User userRetrived= userService.getUser(user.getEmailAddress());
		assertNotNull(userRetrived);
		assertEquals("Username should match", user.getEmailAddress(), userRetrived.getEmailAddress());
		assertEquals("First Name should match", user.getFirstName(),userRetrived.getFirstName());
		assertEquals("Last Name should match", user.getLastName(),userRetrived.getLastName());
		userRepository.delete(userRetrived);
	}
}
