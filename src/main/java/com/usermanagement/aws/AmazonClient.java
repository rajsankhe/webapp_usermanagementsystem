package com.usermanagement.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.usermanagement.exceptions.FileStorageException;

@Service
@Profile("dev")
public class AmazonClient {
	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;

	@Value("${amazonProperties.bucketName}")
	private String bucketName;

	@Value("${amazonProperties.clientRegion}")
	private String clientRegion;

	@PostConstruct
	private void initializeAmazon() {

		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false))
				.build();

	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName(MultipartFile multiPart) {

		return generateUUID() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private String generateUUID() {

		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(bucketName, fileName, file);
	}

	public String uploadFile(MultipartFile multipartFile) throws FileStorageException {

		String fileUrl = "";

		File file = null;
		String fileName = null;
		try {
			file = convertMultiPartToFile(multipartFile);

			fileName = generateFileName(multipartFile);
			fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
			uploadFileTos3bucket(fileName, file);

		} catch (Exception e) {
			throw new FileStorageException("File not stored in S3 bucket. File name: " + fileName);
		} finally {
			if (file != null) {
				file.delete();
			}

		}

		return fileUrl;
	}

	public void deleteFileFromS3Bucket(String fileUrl) throws FileStorageException {
		String fileName = null;
		try {
			fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			s3client.deleteObject(bucketName, fileName);
		} catch (Exception e) {
			throw new FileStorageException("File not stored in S3 bucket. File name: " + fileName);
		}

	}
}
