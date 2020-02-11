package com.usermanagement.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "file")
@JsonPropertyOrder({ "file_name","id", "url","upload_date" })
public class File {

	public File() {

	}

	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
	@Column(name = "FILE_ID")
	@JsonProperty(value="id",access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@Column(name = "URL")
	@JsonProperty(value = "url",access = JsonProperty.Access.READ_ONLY)
	@NotNull
	private String url;
	
	@JsonIgnore
	private String fileNameStored;
	
	@JsonProperty(value = "upload_date",access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone="America/New_York")
    private String uploadDate;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billId", nullable = false)
	@JsonIgnore
	private Bill bill;
	
	@Column(name = "FILE_NAME")
	@JsonProperty(value = "file_name",access = JsonProperty.Access.READ_ONLY)
	private String originalFileName;
	
	@JsonIgnore
	private long size;
	
	@JsonIgnore
	private String owner;
	
	@JsonIgnore
	private String contentType;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	public String getFileNameStored() {
		return fileNameStored;
	}

	public void setFileNameStored(String fileNameStored) {
		this.fileNameStored = fileNameStored;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	
	
}
