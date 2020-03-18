package com.usermanagement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4292041661110777973L;
	private static final Logger logger = LogManager.getLogger(ResourceNotFoundException.class);

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message);
	}

	public ResourceNotFoundException(String message) {
		super(message);
		logger.error(message);
	}
}
