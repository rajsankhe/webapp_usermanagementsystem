package com.usermanagement.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="UserData")
public class User {
	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

	@JsonProperty("first_name")
	@NotBlank(message = "First name is mandatory")
    private String firstName;
	
	@JsonProperty("last_name")
	@NotBlank(message = "Last name is mandatory")
    private String lastName;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "Password is mandatory")
    private String password;
	
	@JsonProperty("email_address")
	@NotBlank(message = "Email Address is mandatory")
	@Pattern(message = "Invalid email id", regexp = "^.+@.+\\..+$")
    private String emailAddress;
    
	@JsonProperty("account_created")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone="America/New_York")
	@CreationTimestamp
    private LocalDateTime accountCreated;
    
	@JsonProperty("account_updated")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone="America/New_York")
	@UpdateTimestamp
    private LocalDateTime accountUpdated;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public LocalDateTime getAccountCreated() {
		return accountCreated;
	}

	public void setAccountCreated(LocalDateTime accountCreated) {
		this.accountCreated = accountCreated;
	}

	public LocalDateTime getAccountUpdated() {
		return accountUpdated;
	}

	public void setAccountUpdated(LocalDateTime accountUpdated) {
		this.accountUpdated = accountUpdated;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", password=" + password
				+ ", emailAddress=" + emailAddress + ", accountCreated=" + accountCreated + ", accountUpdated="
				+ accountUpdated + "]";
	}
    


    
}
