package com.company.security.domain.sms;

public class SmsContext {
	private AuthCode smsValidCode;

	public AuthCode getSmsValidCode() {
		return smsValidCode;
	}

	public void setSmsValidCode(AuthCode smsValidCode) {
		this.smsValidCode = smsValidCode;
	}
	
}
