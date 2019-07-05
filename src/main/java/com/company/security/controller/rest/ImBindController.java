package com.company.security.controller.rest;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.security.Const.LoginServiceConst;
import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.RequestLogin;
import com.company.security.domain.sms.AuthCode;
import com.company.security.domain.sms.SmsContext;
import com.company.security.service.IUserLoginService;
import com.company.security.utils.SecurityKeyService;
import com.xinwei.nnl.common.domain.ProcessResult;
@RestController
@RequestMapping("/userIdno")
public class ImBindController {
	@Resource(name="userLoginService")
	private IUserLoginService userLoginService;
	
	@Resource(name="securityKeyService")
	private SecurityKeyService securityKeyService;
	
	private String bindImkey_prefix="bindImNo";
	
	@RequestMapping(method = RequestMethod.POST,value = "/bindImNo")
	public  ProcessResult bindImNo(@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			String authKey = loginUserSession.getLoginIdType() +":" + loginUserSession.getLoginId();
			//设置秘钥
			PrivateKey rsaPrivateKey = this.getPrivatekey(loginUserSession.getTransid(), bindImkey_prefix);
			accessContext.setRsaPrivateKey(rsaPrivateKey);
			accessContext.setTransid(loginUserSession.getTransid());
			//设置电话号码，transid，authcode
			accessContext.setLoginUserSession(loginUserSession);
			SmsContext smsContext = new SmsContext();
			smsContext.setAuthCode(loginUserSession.getTransid());
			smsContext.setPhone(loginUserSession.getInviteNo());
			AuthCode authCode = new AuthCode();
			authCode.setRandom(loginUserSession.getTransid());
			authCode.setPhone(loginUserSession.getLoginId());
			smsContext.setSmsValidCode(authCode);
			int iRet= userLoginService.bindIdNoByShortRandom(smsContext);
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(accessContext.getLoginUserInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	@RequestMapping(method = RequestMethod.GET,value = "/getRsaPubKey/{type}/{key}")
	public  ProcessResult getRSaPubkeyByKey(@PathVariable String type,@PathVariable String key) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			String authKey = type +":" + key;
			
			AuthCode smsValidCode =new AuthCode();
			
			SmsContext smsContext  = new SmsContext();
			smsContext.setSmsValidCode(smsValidCode);
			int iRet = this.userLoginService.createImUniqueRamdon(smsContext, authKey);
			//用于加密的transid和随机数
			//smsValidCode.setTransid(smsContext.getSmsValidCode().getTransid());
			//smsValidCode.setSendSeqno(smsContext.getSmsValidCode().getAuthCode());
			
			String base64PublicKey = getBase64PublicKey(smsValidCode.getTransid(),bindImkey_prefix);
			//加密的公钥
			smsValidCode.setPublicKey(base64PublicKey);
			processResult.setRetCode(LoginServiceConst.RESULT_Success);
			processResult.setResponseInfo(smsValidCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	/**
	 * 
	 * @param transid
	 * @param phone
	 * @return
	 */
	protected PrivateKey getPrivatekey(String transid,String phone)
	{
		return securityKeyService.getPrivatekey(transid,phone);
		
	}
	
	/**
	 * 
	 * @param request
	 * @param phone
	 * @return
	 */
	protected String getBase64PublicKey(String transid,String phone)
	{
		try {
			PublicKey publicKey = securityKeyService.getPublickey(transid,phone);
			return Base64.encodeBase64String(publicKey.getEncoded());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
}
