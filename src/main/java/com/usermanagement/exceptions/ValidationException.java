package com.usermanagement.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ValidationException extends Exception {

	private static final Logger logger = LogManager.getLogger(ValidationException.class);
	public ValidationException() {
		super();
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message);
	}

	public ValidationException(String message) {
		super(message);
		logger.error(message);
	}
}
