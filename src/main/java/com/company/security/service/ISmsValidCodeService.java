package com.company.security.service;

import com.company.security.domain.AccessContext;
import com.company.security.domain.sms.SmsContext;
import com.company.security.domain.sms.AuthCode;


public interface ISmsValidCodeService {
	/**
	 * 
	 * @param accessContext
	 * @param smsValidCode
	 * @return
	 */
	public int sendValidCodeBySms(SmsContext smsContext, AuthCode smsValidCode);
	/**
	 * 
	 * @param accessContext
	 * @param smsValidCode
	 * @return
	 */
	public int checkValidCodeBySms(SmsContext smsContext, AuthCode smsValidCode);
	
	/**
	 * 获取短信认证码 六位
	 * @return
	 */
	public String getValidCode();
}
