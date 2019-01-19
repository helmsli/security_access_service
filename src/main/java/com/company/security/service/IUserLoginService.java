package com.company.security.service;

import java.security.KeyPair;

import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.sms.AuthCode;
import com.company.security.domain.sms.SmsContext;

public interface IUserLoginService {
	/**
	 * 使用电话号码登录系统
	 * @param AccessContext -- 上下文,需要在调用方设置loginsession信息
	 * @param countryCode
	 * @param phone --全号码countryCode + phone, countrycode which starts with 00
	 * @param password
	 * @param LoginUserSession
	 * @return
	 */
	public int loginUserManual(AccessContext accessContext,String countryCode,String phone,String password,LoginUserSession loginUserSession);
	
	
	/**
	 * 处理通过短信认证码登录的流程
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	public int loginUserBySmsCode(AccessContext accessContext,String countryCode,String phone,LoginUserSession loginUserSession,AuthCode validCode);

	/**
	 * 处理后台自动登录的流程
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	public int loginUserAuto(AccessContext accessContext,String countryCode,String phone,String password);
	
		
	/**
	 * 将验证好的信息注册成为用户
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @return
	 */
	public int registerUserByCode(AccessContext accessContext,String countryCode,String phone,String password,LoginUserSession loginUserSession,AuthCode validCode);
	
	
	/**
	 * 将验证好的信息注册成为用户
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @return
	 */
	public int registerUserByUserName(AccessContext accessContext,String userName,String password,LoginUserSession loginUserSession);
	
	public int regUserNameForServer(AccessContext accessContext, String userName, String password,LoginUserSession loginUserSession);
			
	
	/**
	 * 
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	public int resetPasswrodByPhone(AccessContext accessContext,String countryCode,String phone,String password,LoginUserSession loginUserSession,AuthCode validCode);
	
	/**
	 * 修改用户密码
	 * @param accessContext
	 * @param phone
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public int modifyPasswrodByPhone(AccessContext accessContext, String phone, String oldPassword,String newPassword);
	public int modifyUserInfo(AccessContext accessContext, long userId) ;
	
	public int modifyPasswrodByUserId(AccessContext accessContext, String authKey,long userId,String oldPassword,String newPassword);
	
	
	/**
	 * 根据电话号码生成transid和random 
	 * @param smsContext
	 * @param phone
	 * @return --authcode对象；
	 */
	public int createRandom(SmsContext smsContext,String phone); 
	
	/**
	 * 根据电话号码和事务号，查询随机数，用于校验
	 * @param phone
	 * @param transid
	 * @return
	 */
	public String getRandom(String phone,String transid); 
	
	/**
	 * 
	 * @param smsContext
	 * @param phone
	 * @return
	 */
	public  KeyPair getRsaInfo(String phone); 
	/**
	 * 获取登录的用户信息
	 * @param accessContext
	 * @param phone
	 * @return
	 */
	public	int   getUserInfo(AccessContext accessContext,String phone);
	
	
	
	public	int   getUserInfoByUserId(AccessContext accessContext,String userId);
	
	/**
	 * 修改用户基本信息，哪个字段不为空，修改哪个字段
	 * @param accessContext
	 * @param phone
	 * @return
	 */
	public	int   modifyUserInfo(AccessContext accessContext,String phone);
	
	public LoginUser getLoginUserByUserName(String userName);
}
