package com.usermanagement.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2426512987611470230L;
	private static final Logger logger = LogManager.getLogger(FileStorageException.class);

	public FileStorageException(String message) {
		super(message);
		logger.error(message);
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}