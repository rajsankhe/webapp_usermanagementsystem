package com.usermanagement.util.file.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.usermanagement.aws.AmazonClient;
import com.usermanagement.exceptions.FileStorageException;
import com.usermanagement.util.file.storage.FileStorageUtil;


@Component
@Scope(value = "singleton")
@Profile("dev")
public class DevFileStorageUtil implements FileStorageUtil {

	@Autowired
	private AmazonClient amazonClient;

	@Override
	public String storeFile(MultipartFile file) throws FileStorageException {
		return amazonClient.uploadFile(file);
	}

	@Override
	public void deleteFile(String fileUrl) throws FileStorageException {
		amazonClient.deleteFileFromS3Bucket(fileUrl);
	}

}
