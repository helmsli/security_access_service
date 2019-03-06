package com.company.security.domain.sms;

import java.io.Serializable;

public class SmsContext implements Serializable{
	
	private AuthCode smsValidCode;
	
    private String  authCode;
    
    private String phone;
    
    private String countryCode;
    
	public AuthCode getSmsValidCode() {
		return smsValidCode;
	}

	public void setSmsValidCode(AuthCode smsValidCode) {
		this.smsValidCode = smsValidCode;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
