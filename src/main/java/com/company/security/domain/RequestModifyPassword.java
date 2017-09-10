package com.company.security.domain;

import com.company.security.domain.sms.AuthCode;

public class RequestModifyPassword extends AuthCode{
	/**
	 * 修改秘密是老的密码，短信认证码修改是输入短信认证码
	 */
	private String phone;
	private String modifyKey;
	private String newPassword;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	} 
	
}
