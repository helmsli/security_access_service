package com.company.security.domain;

public class RequestLogin extends LoginUserSession {
	
	/**
	 * 登录交易号，如果是密码登录，可以随便填写，如果是短信认证码登录，需要填写短信交易号
	 */
	private String transid;
	
	/** 密码登录 */
	private String password;	
	/**
	 * 手机登录国家码
	 */
	private String countryCode;
	
	/** 短信认证码登录 */
	private String authCode;	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getTransid() {
		return transid;
	}
	public void setTransid(String transid) {
		this.transid = transid;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
    
}
