package com.company.security.service.impl;

import java.util.HashMap;  
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.company.security.Const.LoginServiceConst;
import com.company.security.domain.sms.SmsContext;
import com.company.security.domain.sms.AuthCode;
import com.company.security.service.ISmsValidCodeService;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.nnl.common.util.JsonUtil;
@Service("smsValidCodeService")
public class SmsValidCodeServiceImpl implements ISmsValidCodeService,InitializingBean {
	@Resource (name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	@Autowired
	protected RestTemplate restTemplate;
	@Value("${alidayu.transferKey}")  
	private String transferKey;
	/**
	 * 短信认证码有效期
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${smsValidCode.durSeconds}")  
	private int smsValidCodeDurSeconds;
	
	@Value("${smsService.Url}")  
	private String smsServerUrl;
	
	
	@Value("${alidayu.templateCode}")  
	private String templateCodes;
	@Value("${alidayu.appName:cootalk}")  
	private String registerAppname;
	
	private Map<String,String>templateMaps = new ConcurrentHashMap<String,String>();
	
	
	
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
		//todo:
		//smsValidCode.setAuthCode("");
		smsContext.setSmsValidCode(smsValidCode);
		sendSms(smsContext);
		//异步调用阿里大鱼服务发送短信；need to do;
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
	
	protected ProcessResult sendSms(SmsContext smsContext)
	{
		try {
			String url = this.smsServerUrl+ "/smsService/coojisu/0086";
			SmsInfo smsInfo = new SmsInfo();
			
			smsInfo.setTransId(String.valueOf(System.currentTimeMillis()));
			smsInfo.setDestAppName(registerAppname);
			smsInfo.setSmsTemplateCode(templateMaps.get(String.valueOf(smsContext.getSmsValidCode().getBizType())));
			smsInfo.setCountryCode(	smsContext.getSmsValidCode().getCountryCode());
			smsInfo.setCalledPhoneNumbers(smsContext.getSmsValidCode().getPhone());
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("code", smsContext.getAuthCode());
			smsInfo.setParameters(JsonUtil.toJson(paraMap));
			smsInfo.setCheckCrc(SecuritySmsAlgorithm.getCrcString(transferKey, smsInfo));
			ProcessResult ret = this.restTemplate.postForObject(url, smsInfo, ProcessResult.class);
			logger.debug(ret.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("",e);
			
		}

		return null;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		String[] templateInfos = this.templateCodes.split(",");
		for(int i =0;i<templateInfos.length;i++)
		{
			String templateInfoString=templateInfos[i];
			String []templateInfo = templateInfoString.split(":");
			this.templateMaps.put(templateInfo[0].toLowerCase(), templateInfo[1].trim());
			
		}
	}

}
