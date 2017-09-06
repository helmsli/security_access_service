package com.company.security.service;

import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUserSession;

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
	public int loginUserManual(AccessContext accessContext,String countryCode,String phone,String password);
	
	/**
	 * 将验证好的信息注册成为用户
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @return
	 */
	public int registerUserByPhone(AccessContext accessContext,String countryCode,String phone,String password);
	
	/**
	 * 
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	public int resetPasswrodByPhone(AccessContext accessContext,String countryCode,String phone,String password);
	
}
