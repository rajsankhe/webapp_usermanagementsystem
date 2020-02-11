package com.usermanagement.util.file.storage;

import org.springframework.web.multipart.MultipartFile;

import com.usermanagement.exceptions.FileStorageException;

public interface FileStorageUtil {

	public String storeFile(MultipartFile file) throws FileStorageException;

	public void deleteFile(String fileUrl) throws FileStorageException;

}
