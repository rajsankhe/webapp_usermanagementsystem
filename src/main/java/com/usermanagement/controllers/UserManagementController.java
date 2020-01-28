package com.usermanagement.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.exceptions.ValidationException;
import com.usermanagement.models.User;
import com.usermanagement.services.UserService;

@RestController
@RequestMapping("/user")
public class UserManagementController {

	@Autowired
	private UserService userService;

	@PostMapping()
	public ResponseEntity<User> newUser(@Valid @RequestBody User newUser) throws ValidationException {
		return new ResponseEntity<User>(userService.save(newUser),HttpStatus.CREATED);
	}

	@PutMapping("/self")
	protected ResponseEntity updateUser(@Valid @RequestBody User updateUser, Authentication  authentication ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			userService.updateUser(updateUser, authentication.getName());
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/self")
	protected ResponseEntity<User> getUser(Authentication  authentication ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<User>(userService.getUser(authentication.getName()),HttpStatus.OK);
		}
		return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
	}
}