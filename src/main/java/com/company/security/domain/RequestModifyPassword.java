package com.company.security.domain;

import com.company.security.domain.sms.AuthCode;

public class RequestModifyPassword extends AuthCode{
	
	//private String phone;
	/**
	 * 修改秘密是老的密码，短信认证码修改是输入短信认证码
	 */
	private String modifyKey;
	private String newPassword;
	/**
	 * 当前登录的名字的类型
	 */
	private int loginIdType=LoginUserSession.LoginIdType_phone;
	/** 登录的用户标识.*/
	private String loginId;
	
	public String getModifyKey() {
		return modifyKey;
	}
	public void setModifyKey(String modifyKey) {
		this.modifyKey = modifyKey;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	/*
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	*/
	@Override
	public String toString() {
		return "RequestModifyPassword [phone=" + this.getPhone() + ", modifyKey=" + modifyKey + ", newPassword=" + newPassword
				+ "]" + super.toString();
	}
	public int getLoginIdType() {
		return loginIdType;
	}
	public void setLoginIdType(int loginIdType) {
		this.loginIdType = loginIdType;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	} 
	
}
