package com.company.security.domain;

import java.io.Serializable;


public class LoginUser implements Serializable{
	/** 用户ID. */
	private long userId;
	/** 登录的用户名字.*/
	private String loginName;
	
	/** 显示名字. */
	private String displayName;
	
	/** 电话号码. 带国家码的号码，国家码开头是00*/
	private String phone;

	/** 电话号码国家码. */
	private String phoneCode;
	/** 电话号码认证状态. */
	private int phoneVerified;
	/** 头像地址. */
	private String avatar;
	/** 性别. */
	private int sex;
	/** 邮箱. */
	private String email;

	/** 邮箱认证状态. */
	private int emailVerified;

	/** 密码. */
	private String password;

	/** 密码扩展字段. */
	private String passwordExt;
	
	/** 账户状态. */
	private int status;

	/** 拥有的角色. */
	private String roles;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginname) {
		this.loginName = loginname;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayname) {
		this.displayName = displayname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneCode() {
		return phoneCode;
	}
	public void setPhoneCode(String phoneccode) {
		this.phoneCode = phoneccode;
	}
	public int getPhoneVerified() {
		return phoneVerified;
	}
	public void setPhoneVerified(int phoneverified) {
		this.phoneVerified = phoneverified;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getEmailVerified() {
		return emailVerified;
	}
	public void setEmailVerified(int emailverified) {
		this.emailVerified = emailverified;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordExt() {
		return passwordExt;
	}
	public void setPasswordExt(String passwordext) {
		this.passwordExt = passwordext;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "LoginUser [userId=" + userId + ", loginName=" + loginName + ", displayName=" + displayName + ", phone="
				+ phone + ", phoneCode=" + phoneCode + ", phoneVerified=" + phoneVerified + ", avatar=" + avatar
				+ ", sex=" + sex + ", email=" + email + ", emailVerified=" + emailVerified + ", password=" + password
				+ ", passwordExt=" + passwordExt + ", status=" + status + ", roles=" + roles + "]";
	}
	
	
	
	
	
	
	
    
	
}
