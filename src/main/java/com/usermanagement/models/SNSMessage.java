package com.usermanagement.models;

import java.util.List;

public class SNSMessage {
	private String emailId;
	private List<String> dueBills;
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public List<String> getDueBills() {
		return dueBills;
	}
	public void setDueBills(List<String> dueBills) {
		this.dueBills = dueBills;
	}
	
}
