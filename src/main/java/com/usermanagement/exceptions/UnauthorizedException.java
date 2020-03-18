package com.usermanagement.exceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends Exception {

	private static final Logger logger = LogManager.getLogger(UnauthorizedException.class);
	
	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message);
	}

	public UnauthorizedException(String message) {
		super(message);
		logger.error(message);
	}
}
