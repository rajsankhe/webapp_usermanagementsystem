package com.usermanagement.services;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.models.User;
import com.usermanagement.repositories.UserRepository;

import static org.junit.Assert.*;

@SpringBootTest
public class UserManagementServiceTest {

	@Test
	public void TestPassword()
	{
		UserService userService= new UserService();
		String strongPassowrd="Moysa@40983";
		String weakPassowrd="12";
		Assert.assertTrue(userService.checkPassword(strongPassowrd));
		Assert.assertFalse(userService.checkPassword(weakPassowrd));
	}
}
