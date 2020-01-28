package com.usermanagement.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

enum PaymentStatus 
{ 
	@JsonProperty(value = "paid")
	PAID,
	@JsonProperty(value = "due")
	DUE,
	@JsonProperty(value = "past_due")
	PAST_DUE,
	@JsonProperty(value = "no_payment_required")
	NO_PAYMENT_REQUIRED;
} 

@Entity
@Table(name="Bill")
public class Bill {
	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "billId", updatable = false, nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID billId;
	
	@JsonProperty(value = "created_ts",access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone="America/New_York")
	@CreationTimestamp
    private LocalDateTime accountCreated;
    
	@JsonProperty(value = "updated_ts",access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone="America/New_York")
	@UpdateTimestamp
    private LocalDateTime accountUpdated;

	@JsonProperty(value = "owner_id",access = JsonProperty.Access.READ_ONLY)
    private UUID ownerId;
	
	@JsonProperty("vendor")
	@NotBlank(message = "Vendor is mandatory")
    private String vendor;

	@JsonProperty("bill_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="America/New_York")
	@NotNull(message = "Bill Date is mandatory")
    private Date billDate;

	@JsonProperty("due_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone="America/New_York")
	@NotNull(message = "Due Date is mandatory")
    private Date dueDate;
	
	@JsonProperty("amount_due")
	@NotNull(message = "Amount Date is mandatory")
    private Double amountDue;

	@JsonProperty("categories")
	
	@NotEmpty(message = "Categories are mandatory")
	private String categories;
	
	@JsonProperty("paymentStatus")
	@NotNull(message = "paymentStatus is mandatory")
	private PaymentStatus paymentStatus;


	public UUID getBillId() {
		return billId;
	}

	public void setBillId(UUID billId) {
		this.billId = billId;
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

	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String[] getCategories() {
		return categories.split(",");
	}

	public void setCategories(String[] input) {
		this.categories =String.join(",", input) ;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public Double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}
	
}
