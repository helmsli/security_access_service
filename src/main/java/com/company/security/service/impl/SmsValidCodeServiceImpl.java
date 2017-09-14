package com.company.security.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.security.Const.LoginServiceConst;
import com.company.security.domain.sms.SmsContext;
import com.company.security.domain.sms.AuthCode;
import com.company.security.service.ISmsValidCodeService;
@Service("smsValidCodeService")
public class SmsValidCodeServiceImpl implements ISmsValidCodeService {
	@Resource (name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	
	/**
	 * 短信认证码有效期
	 */
	@Value("${smsValidCode.durSeconds}")  
	private int smsValidCodeDurSeconds;
	
	@Override
	public int sendValidCodeBySms(SmsContext smsContext, AuthCode smsValidCode)
	{
		// TODO Auto-generated method stub
		//SmsValidCode smcValidCode = new SmsValidCode();	   
		
		String authCode = this.getValidCode();
		String seqno = getValidCodeSeqNo();
		//如果transid为空，重新申请transid，否则复用原来的transid
		if(StringUtils.isEmpty(smsValidCode.getTransid()))
		{
			smsValidCode.setTransid(getAuthTransId(seqno));
		}
		String validKey = createSmsValidKey(smsValidCode);
		smsValidCode.setAuthCode(authCode);
		smsValidCode.setSendSeqno(seqno);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		
		opsForValue.set(validKey, smsValidCode,smsValidCodeDurSeconds,TimeUnit.SECONDS);
		smsContext.setAuthCode(smsValidCode.getAuthCode());
		System.out.println(smsValidCode);
		smsValidCode.setAuthCode("");
		smsContext.setSmsValidCode(smsValidCode);
		return 0;
	}

	@Override
	public int checkValidCodeBySms(SmsContext smsContext, AuthCode smsValidCode) {
		// TODO Auto-generated method stub
		String validKey = createSmsValidKey(smsValidCode);
		try {
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			AuthCode cacheSmsValidCode = (AuthCode)opsForValue.get(validKey);
			if(cacheSmsValidCode!=null && smsValidCode.isSaveAuthCode(cacheSmsValidCode))
			{
				return LoginServiceConst.RESULT_Success;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return LoginServiceConst.RESULT_Error_ValidCode;
	}
	@Override
	public String getValidCode()
	{
		int mobile_code = (int)((Math.random()*9+1)*100000);
		if(mobile_code>999999)
		{
			String ret = String.valueOf(mobile_code);
			return ret.substring(0, 7);
		}
		return String.valueOf(mobile_code);
	}
	/**
	 * 生成短信发送序号
	 */
	public String getValidCodeSeqNo()
	{
		int mobile_code = (int)((Math.random()*9+1)*10);
		if(mobile_code>99)
		{
			String ret = String.valueOf(mobile_code);
			return ret.substring(0, 2);
		}
		return String.valueOf(mobile_code);
	}
	
	/**
	 * 获取transid
	 * @param phone
	 * @return
	 */
	public String getAuthTransId(String seqNo)
	{
		long transId =  System.currentTimeMillis()-1504369881000l;
		return transId+"*"+seqNo;
	}
	
	/**
	 * 生成rediskey
	 * @param smcValidCode
	 * @return
	 */
	protected synchronized String createSmsValidKey(AuthCode smcValidCode)
	{	
		
		StringBuilder str = new StringBuilder();
		str.append("smsValid:");
		str.append(smcValidCode.getPhone());
		str.append("*");
		str.append(smcValidCode.getTransid());
		return str.toString();
	}
	

}
