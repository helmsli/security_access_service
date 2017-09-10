package com.company.security.service.impl;

public class SecurityUserCacheKeyService {
	
	public static final String Key_prefix_LoginUser= "luser:";
	public static final String Key_prefix_phone= "lphone:";
	public static final String Key_prefix_email= "lemail:";
	public static final String Key_prefix_lastModify= "lmodify:";
	public static final String Key_prefix_UserLock= "lulock:";
	public static final String Key_prefix_DeviceType= "luDType:";
	public static final String Key_prefix_TokenExpired= "luExpir:";
	public static final String Key_prefix_CreateUserid= "luserCreate:";
	
	public static final String Key_prefix_Split=":";
	
	/**
	 * 创建userID的key
	 * @return
	 */
	public String getCreateUserIdkey()
	{
		StringBuilder str= new StringBuilder();
		str.append(Key_prefix_CreateUserid);
		return str.toString();
	}
	
	/**
	 * 根据登录类型获取token的key
	 * @param loginType -- pad/mobile/web
	 * @param userid
	 * @return
	 */
	public String getTokenKey(int loginType,long userid)
	{
		StringBuilder str= new StringBuilder();
		str.append(Key_prefix_DeviceType);
		str.append(userid);
		str.append(Key_prefix_Split);
		str.append(loginType);
		return str.toString();
	}
	public String getTokenAccessKey(String token)
	{
		StringBuilder str= new StringBuilder();
		str.append(Key_prefix_TokenExpired);
		str.append(token);
		return str.toString();
	}
	
	/**
	 * 获取同步锁的key
	 * @param userId
	 * @return
	 */
	public String getLockkey(long userId)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_UserLock);
		retStr.append(userId);
		return retStr.toString();
	}
	
	public String getUserSessionkey(long userId)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_UserLock);
		retStr.append(userId);
		return retStr.toString();
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getLoginUserkey(long userId)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_LoginUser);
		retStr.append(userId);
		return retStr.toString();
	}
	
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public String getPhoneKey(String phone)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_phone);
		retStr.append(phone);
		return retStr.toString();
	}
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public String getEmailKey(String email)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_email);
		retStr.append(email);
		return retStr.toString();
	}
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getLastModifyKey(long userId)
	{
		StringBuilder retStr = new StringBuilder();
		retStr.append(Key_prefix_lastModify);
		retStr.append(userId);
		return retStr.toString();
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getLastModifyKeyPhone(String phone)
	{
		StringBuilder retStr = new StringBuilder();
		//用两个分隔符和userid区分
		retStr.append(Key_prefix_lastModify);
		retStr.append(Key_prefix_lastModify);
		retStr.append(phone);
		return retStr.toString();
	}
}
