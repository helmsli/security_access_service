package com.company.security.domain.sms;

public class AuthCode {
	
	private String phone;
	/**
	 * 短信交易号
	 */
	private String transid;
	/**
	 * 发送的序号
	 */
	private String sendSeqno;
	/**
	 * 校验码
	 */
	private String authCode;
	
		
	public String getTransid() {
		return transid;
	}
	public void setTransid(String transid) {
		this.transid = transid;
	}
	public String getSendSeqno() {
		return sendSeqno;
	}
	public void setSendSeqno(String sendSeqno) {
		this.sendSeqno = sendSeqno;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public boolean isSaveAuthCode(AuthCode destAuthCode)
	{
		try {
			return this.authCode.equalsIgnoreCase(destAuthCode.getAuthCode())&& this.transid.equalsIgnoreCase(destAuthCode.getTransid()) && this.phone.equalsIgnoreCase(destAuthCode.getPhone());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
