package com.usermanagement.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.usermanagement.services.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserService userService;

	@Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
  
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (userService.auhorizeRequest(userName, password))
        {
        	return new UsernamePasswordAuthenticationToken(
        			userName, password, new ArrayList<>());
        }
        else
        {
        	throw new BadCredentialsException("External system authentication failed");
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
