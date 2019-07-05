package com.company.security.domain;

import java.io.Serializable;
import java.util.Date;

public class LoginUserSession implements Serializable{
	/**
	 * email登录
	 */
	public static final int LoginIdType_email = 1;
	/**
	 * 电话登录
	 */
	public static final int LoginIdType_phone = 2;
	/**
	 * 用户名登录
	 */
	public static final int LoginIdType_userName = 3;
	
	/**
	 * 身份证或者护照，不能修改，在db中有实现。
	 */
	public static final int LoginIdType_userIdNo = 4;
	public static final int LoginIdType_userIdPassport = 5;
	
	/**
	 * 用户名登录
	 */
	//public static final int LoginIdType_userid = 6;
	public static final int LoginIdType_userId = 7;
	
	public static final int LoginIdType_wchatId = 8;
	
	public static final int LoginIdType_zhibubaoId = 9;
	
	
	
	/**
	 * pcweb登录
	 */
	public static final int loginType_web = 1;
	/**
	 * 移动端登录
	 */
	public static final int loginType_mobile = 2;
	/**
	 * 
	 */
	public static final int loginType_pad = 3;
	
	/**
	 * pc客户端
	 */
	public static final int loginType_pc = 4;
	
	/**
	 * 登录的渠道名字，PC/WEB/mobile/pad
	 */
	private int loginType;
	
	/**
	 * 当前登录的名字的类型
	 */
	private int loginIdType;
	/** 登录的用户标识.*/
	private String loginId;
	
	/**
	 * 登录的用户ID
	 */
	private long userId;
	
	/**
	 * 登录的token信息
	 */
	private String token;
	/**
	 * 登录时间
	 */
	private Date loginTime;
	
	/**
	 * 登录的设备名称
	 */
	private int loginDeviceBrand;
	/**
	 * 登录的设备ID
	 */
	private String loginDeviceId="";
	
	private String ip;
	
	private String role;
	
	private String inviteNo;
	
	
	public static final int loginManul_auto =0;
	public static final int loginManul_manual =1;
	
	/**
	 * 0 自动登录，1-手动登录
	 */
	private int loginManul =0;
	
	
	public String getInviteNo() {
		return inviteNo;
	}
	public void setInviteNo(String inviteNo) {
		this.inviteNo = inviteNo;
	}
	
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public int getLoginType() {
		return loginType;
	}
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getLoginDeviceId() {
		return loginDeviceId;
	}
	public void setLoginDeviceId(String loginDeviceId) {
		this.loginDeviceId = loginDeviceId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	public int getLoginIdType() {
		return loginIdType;
	}
	public void setLoginIdType(int loginIdType) {
		this.loginIdType = loginIdType;
	}
	
	@Override
	public String toString() {
		return "LoginUserSession [loginType=" + loginType + ", loginIdType=" + loginIdType + ", loginId=" + loginId
				+ ", userId=" + userId + ", token=" + token + ", loginTime=" + loginTime + ", loginDeviceBrand="
				+ loginDeviceBrand + ", loginDeviceId=" + loginDeviceId + ", ip=" + ip + ", role=" + role + "]";
	}
	public int getLoginDeviceBrand() {
		return loginDeviceBrand;
	}
	public void setLoginDeviceBrand(int loginDeviceBrand) {
		this.loginDeviceBrand = loginDeviceBrand;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getLoginManul() {
		return loginManul;
	}
	public void setLoginManul(int loginManul) {
		this.loginManul = loginManul;
	}
	
	
}
