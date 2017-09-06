package com.company.security.domain;

import java.io.Serializable;
import java.util.Date;

public class LoginUser implements Serializable{
	/** 用户ID. */
	private long userId;
	/** 登录的用户名字.*/
	private String loginname;
	
	/** 显示名字. */
	private String displayname;
	
	/** 电话号码. 带国家码的号码，国家码开头是00*/
	private String phone;

	/** 电话号码国家码. */
	private String phoneccode;
	/** 电话号码认证状态. */
	private int phoneverified;
	/** 头像地址. */
	private String avatar;
	/** 性别. */
	private int sex;
	/** 邮箱. */
	private String email;

	/** 邮箱认证状态. */
	private int emailverified;

	/** 密码. */
	private String password;

	/** 密码扩展字段. */
	private String passwordext;
	
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
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneccode() {
		return phoneccode;
	}
	public void setPhoneccode(String phoneccode) {
		this.phoneccode = phoneccode;
	}
	public int getPhoneverified() {
		return phoneverified;
	}
	public void setPhoneverified(int phoneverified) {
		this.phoneverified = phoneverified;
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
	public int getEmailverified() {
		return emailverified;
	}
	public void setEmailverified(int emailverified) {
		this.emailverified = emailverified;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordext() {
		return passwordext;
	}
	public void setPasswordext(String passwordext) {
		this.passwordext = passwordext;
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
		return "LoginUser [userId=" + userId + ", loginname=" + loginname 
				+ ", displayname=" + displayname + ", phone=" + phone + ", phoneccode=" + phoneccode
				+ ", phoneverified=" + phoneverified + ", avatar=" + avatar + ", sex=" + sex + ", email=" + email
				+ ", emailverified=" + emailverified + ", password=" + password + ", passwordext=" + passwordext
				+ ", status=" + status + ", roles=" + roles + "]";
	}
	
    
	
}
