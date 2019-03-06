package com.company.security.service.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SmsInfo implements Serializable{
	
	/**
	 * 请求发送短信的APP的Name
	 */
	private String requestAppName;
	
	//接收短信的APP的名字
	private String destAppName;
		
	//发送的短信类型
	private String smsTemplateCode;
	//发送的事务号
	private String transId;
	//发送的短信的动态参数
	private String parameters;
	//发送的电话号码的国家码
	private String countryCode;
	//发送的电话号码
	private String calledPhoneNumbers;
	//短信过期时间，接收到几秒过期
	private int  expireTimeSecond;
	private Date requestTime=Calendar.getInstance().getTime();
	//参数校验和
	private String checkCrc;
	
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	
	
	public String getSmsTemplateCode() {
		return smsTemplateCode;
	}
	public void setSmsTemplateCode(String smsTemplateCode) {
		this.smsTemplateCode = smsTemplateCode;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCalledPhoneNumbers() {
		return calledPhoneNumbers;
	}
	public void setCalledPhoneNumbers(String calledPhoneNumbers) {
		this.calledPhoneNumbers = calledPhoneNumbers;
	}
	public int getExpireTimeSecond() {
		return expireTimeSecond;
	}
	public void setExpireTimeSecond(int expireTimeSecond) {
		this.expireTimeSecond = expireTimeSecond;
	}
	public String getCheckCrc() {
		return checkCrc;
	}
	public void setCheckCrc(String checkCrc) {
		this.checkCrc = checkCrc;
	}
	public String getRequestAppName() {
		return requestAppName;
	}
	public void setRequestAppName(String requestAppName) {
		this.requestAppName = requestAppName;
	}
	public String getDestAppName() {
		return destAppName;
	}
	public void setDestAppName(String destAppName) {
		this.destAppName = destAppName;
	}
	@Override
	public String toString() {
		return "SmsInfo [requestAppName=" + requestAppName + ", destAppName=" + destAppName + ", smsTemplateCode="
				+ smsTemplateCode + ", transId=" + transId + ", parameters=" + parameters + ", countryCode="
				+ countryCode + ", calledPhoneNumbers=" + calledPhoneNumbers + ", expireTimeSecond=" + expireTimeSecond
				+ ", checkCrc=" + checkCrc + "]";
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	
}
