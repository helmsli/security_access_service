package com.company.security.domain;

import java.util.Date;

public class LoginUserSession {
	/**
	 * email登录
	 */
	public static final int LoginIdType_email = 1;
	/**
	 * 电话登录
	 */
	public static final int LoginIdType_phone = 2;
	/**
	 * id登录
	 */
	public static final int LoginIdType_userid = 3;
	
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
	 * 当前登录的名字的类型
	 */
	private int loginType;
	
	
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
	private String loginDeviceId;
	
	private String ip;
	
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
				+ ", userId=" + userId + ", token=" + token + ", loginTime=" + loginTime + ", loginDeviceType="
				+ this.loginDeviceBrand + ", loginDeviceId=" + loginDeviceId + ", ip=" + ip + "]";
	}
	public int getLoginDeviceBrand() {
		return loginDeviceBrand;
	}
	public void setLoginDeviceBrand(int loginDeviceBrand) {
		this.loginDeviceBrand = loginDeviceBrand;
	}
	
	
}
