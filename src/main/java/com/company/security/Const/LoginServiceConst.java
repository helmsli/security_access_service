package com.company.security.Const;

public class LoginServiceConst {
	public static final int RESULT_Success = 0;	
	public static final int RESULT_Error_Fail = -1;	
	
	//范围3000到4000
	public static final int RESULT_ERROR_START = 3000;

	/**
	 * 用户名或者密码错误
	 */
	public static final int RESULT_Error_PasswordError = RESULT_ERROR_START + 1;
	/**
	 * 更新缓存错误
	 */
	public static final int RESULT_Error_putSession = RESULT_ERROR_START + 2;
	/**
	 * 电话号码已经注册
	 */
	public static final int RESULT_Error_PhoneHaveRegister = RESULT_ERROR_START + 3;
	/**
	 * 认证码错误
	 */
	public static final int RESULT_Error_ValidCode = RESULT_ERROR_START + 4;
	
	/**
	 * 电话号码已经注册
	 */
	public static final int RESULT_Error_UserNameHaveRegister = RESULT_ERROR_START + 5;
}
