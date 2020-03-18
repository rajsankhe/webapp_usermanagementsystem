package com.usermanagement.util.file.storage.impl;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.usermanagement.exceptions.FileStorageException;
import com.usermanagement.properties.FileStorageProperties;
import com.usermanagement.util.file.storage.FileStorageUtil;

@Component
@Scope(value = "singleton")
@Profile("default")
public class DefaultFileStorageUtil implements FileStorageUtil {

	private final Path fileStorageLocation;

	public DefaultFileStorageUtil(FileStorageProperties fileStorageProperties) throws FileStorageException {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	@Override
	public String storeFile(MultipartFile file) throws FileStorageException {

		String fileName = generateFileName(file);

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return targetLocation.toString();
		} catch (Exception ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	@Override
	public void deleteFile(String fileUrl) throws FileStorageException {

		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileUrl);
			Files.delete(targetLocation);
		} catch (Exception e) {
			throw new FileStorageException("Could not delete file " + fileUrl + ". Please try again!", e);
		}
	}

	private String generateFileName(MultipartFile multiPart) {
		return generateUUID() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private String generateUUID() {

		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
