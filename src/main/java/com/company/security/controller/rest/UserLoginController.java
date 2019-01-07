package com.company.security.controller.rest;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.commons.codec.binary.Base64;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import com.company.security.Const.LoginServiceConst;
import com.company.security.Const.SessionKeyConst;
import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.RequestLogin;
import com.company.security.domain.RequestModifyPassword;
import com.company.security.domain.RequestTokenBody;
import com.company.security.domain.SecurityUser;
import com.company.security.domain.sms.SmsContext;
import com.company.security.domain.sms.AuthCode;
import com.company.security.service.ISmsValidCodeService;
import com.company.security.service.IUserLoginService;
import com.company.security.service.SecurityUserCacheService;
import com.company.security.utils.RSAUtils;
import com.company.security.utils.SecurityKeyService;
import com.google.gson.Gson;
import com.xinwei.nnl.common.domain.ProcessResult;
import com.xinwei.nnl.common.util.JsonUtil;


@RestController
@RequestMapping("/user")
public class UserLoginController {
	
	@Resource(name="userLoginService")
	private IUserLoginService userLoginService;
	
	@Resource(name="smsValidCodeService")
	private ISmsValidCodeService smsValidCodeService;
	
	@Resource(name="securityKeyService")
	private SecurityKeyService securityKeyService;
	
	/**
	 * 认证码注册
	 * @param request
	 * @param countryCode
	 * @param requestTokenBody
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/registerByCode")
	public  ProcessResult registerUserByCode(@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			//设置秘钥
			PrivateKey rsaPrivateKey = this.getPrivatekey(loginUserSession.getTransid(), loginUserSession.getLoginId());
			accessContext.setRsaPrivateKey(rsaPrivateKey);
			accessContext.setTransid(loginUserSession.getTransid());
			//设置电话号码，transid，authcode
			AuthCode authCode = new AuthCode();
			authCode.setTransid(loginUserSession.getTransid());
			authCode.setAuthCode(loginUserSession.getAuthCode());
			authCode.setPhone(loginUserSession.getLoginId());
			accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.registerUserByCode(accessContext, countryCode, loginUserSession.getLoginId(), loginUserSession.getPassword(),loginUserSession,authCode);
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(accessContext.getLoginUserInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST,value = "/registerByUserName")
	public  ProcessResult registerUserByUserName(@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			String authKey = loginUserSession.getLoginIdType() +":" + loginUserSession.getLoginId();
			try {
				
				if(loginUserSession.getLoginIdType()==LoginUserSession.LoginIdType_phone)
				{
					authKey =loginUserSession.getLoginId();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//设置秘钥
			PrivateKey rsaPrivateKey = this.getPrivatekey(loginUserSession.getTransid(), authKey);
			accessContext.setRsaPrivateKey(rsaPrivateKey);
			accessContext.setTransid(loginUserSession.getTransid());
			//设置电话号码，transid，authcode
			accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.registerUserByUserName(accessContext,loginUserSession.getLoginId(), loginUserSession.getPassword(),loginUserSession);
					
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(accessContext.getLoginUserInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	
	/**
	 * 密码登录
	 * @param countryCode
	 * @param loginUserSession -- phone,password
	 * @return
	 */
	@RequestMapping(method = {RequestMethod.POST,RequestMethod.GET},value = "/{countryCode}/loginByPass")
	public  ProcessResult loginByPass(@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			String authKey = loginUserSession.getLoginIdType() +":" + loginUserSession.getLoginId();
			try {
				
				if(loginUserSession.getLoginIdType()==LoginUserSession.LoginIdType_phone)
				{
					authKey =loginUserSession.getLoginId();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PrivateKey rsaPrivateKey = getPrivatekey(loginUserSession.getTransid(),authKey);
			accessContext.setRsaPrivateKey(rsaPrivateKey);
			accessContext.setTransid(loginUserSession.getTransid());
			accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.loginUserManual(accessContext, countryCode, loginUserSession.getLoginId(), loginUserSession.getPassword(),loginUserSession);
			processResult.setRetCode(iRet);
			loginUserSession.setPassword("");
			if(iRet==0)
			{
				loginUserSession.setAvatar(accessContext.getLoginUserInfo().getAvatar());
				loginUserSession.setDisplayName(accessContext.getLoginUserInfo().getDisplayName());
				loginUserSession.setRole(accessContext.getLoginUserInfo().getRoles());
				accessContext.getLoginUserInfo().setPassword("");
				
			}
			processResult.setResponseInfo(loginUserSession);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	/**
	 * 短信认证码登录流程
	 * @param request
	 * @param userId
	 * @param token
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/loginByAuthCode")
	public  ProcessResult loginByAuthCode(@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			//构造短信认证码
			AuthCode authCode = new AuthCode();
			authCode.setPhone(loginUserSession.getLoginId());
			authCode.setTransid(loginUserSession.getTransid());
			authCode.setAuthCode(loginUserSession.getAuthCode());
			accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.loginUserBySmsCode(accessContext, countryCode, loginUserSession.getLoginId(),loginUserSession,authCode);
			processResult.setRetCode(iRet);
			loginUserSession.setPassword("");
			if(iRet==0)
			{
				loginUserSession.setAvatar(accessContext.getLoginUserInfo().getAvatar());
				loginUserSession.setDisplayName(accessContext.getLoginUserInfo().getDisplayName());
				accessContext.getLoginUserInfo().setPassword("");
				loginUserSession.setRole(accessContext.getLoginUserInfo().getRoles());
			}
			processResult.setResponseInfo(loginUserSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	/**
	 * 申请短信认证码，通用的函数,所有发送短信的都可以走这个短信认证码
	 * @param requestTokenBody  -- 申请的可以不走这个流程
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/getSmsValid")
	public  ProcessResult getSmsValidCode(@RequestBody AuthCode authCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AuthCode smsValidCode =authCode;
			//禁止客户端的transid
			smsValidCode.setTransid("");
			
			SmsContext smsContext  = new SmsContext();
			smsContext.setSmsValidCode(smsValidCode);
			//申请随机数
			int iRet = this.userLoginService.createRandom(smsContext, smsValidCode.getPhone());
			//发送短信认证码
			 iRet = smsValidCodeService.sendValidCodeBySms(smsContext, smsValidCode);
			//构造秘钥
			 String base64PublicKey = getBase64PublicKey(smsContext.getSmsValidCode().getTransid(),smsValidCode.getPhone());
			smsContext.getSmsValidCode().setCrcType(AuthCode.CrcType_RSA);
			smsContext.getSmsValidCode().setPublicKey(base64PublicKey);
			processResult.setRetCode(iRet);
			//todo:测试阶段，先返回给客户端，不用真的发送短信，用于测试
			//smsContext.getSmsValidCode().setAuthCode("");
			processResult.setResponseInfo(smsContext.getSmsValidCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	/**
	 * 客户端申请随机数，用于加密
	 * @param AuthCode
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/getRandom")
	public  ProcessResult getRandom(@RequestBody AuthCode authCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AuthCode smsValidCode =authCode;
			SmsContext smsContext  = new SmsContext();
			smsContext.setSmsValidCode(smsValidCode);
			int iRet = this.userLoginService.createRandom(smsContext, smsValidCode.getPhone());
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(smsContext.getSmsValidCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	
	/**
	 * 校验短信认证码
	 * @param AuthCode
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "{countryCode}/checkAuthCode")
	public  ProcessResult checkRandom(@PathVariable String countryCode,@RequestBody AuthCode authCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_ValidCode);
		try {
			
			SmsContext smsContext  = new SmsContext();		
			int iRet = smsValidCodeService.checkValidCodeBySms(smsContext, authCode);
			processResult.setRetCode(iRet);
			
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
	
	/**
	 * 获取加密的公钥
	 * @param request
	 * @param AuthCode
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/getRsaPubKey")
	public  ProcessResult getRSaPubkey(@RequestBody AuthCode authCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AuthCode smsValidCode =authCode;
			SmsContext smsContext  = new SmsContext();
			smsContext.setSmsValidCode(authCode);
			int iRet = this.userLoginService.createRandom(smsContext, smsValidCode.getPhone());
			//用于加密的transid和随机数
			//smsValidCode.setTransid(smsContext.getSmsValidCode().getTransid());
			//smsValidCode.setSendSeqno(smsContext.getSmsValidCode().getAuthCode());
			
			String base64PublicKey = getBase64PublicKey(smsValidCode.getTransid(),smsValidCode.getPhone());
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
	
	@RequestMapping(method = RequestMethod.GET,value = "/getRsaPubKey/{type}/{key}")
	public  ProcessResult getRSaPubkeyByKey(@PathVariable String type,@PathVariable String key) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			String authKey = type +":" + key;
			try {
				int iType = Integer.parseInt(type);
				if(iType==LoginUserSession.LoginIdType_phone)
				{
					authKey =key;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AuthCode smsValidCode =new AuthCode();
			
			SmsContext smsContext  = new SmsContext();
			smsContext.setSmsValidCode(smsValidCode);
			int iRet = this.userLoginService.createRandom(smsContext, authKey);
			//用于加密的transid和随机数
			//smsValidCode.setTransid(smsContext.getSmsValidCode().getTransid());
			//smsValidCode.setSendSeqno(smsContext.getSmsValidCode().getAuthCode());
			
			String base64PublicKey = getBase64PublicKey(smsValidCode.getTransid(),authKey);
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
	 * 短信认证码重置密码
	 * @param requestTokenBody
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/resetPassByAuthCode")
	public  ProcessResult resetPassByAuthCode(@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext = new AccessContext();
			accessContext.setTransid(loginUserSession.getTransid());
			PrivateKey privateKey=getPrivatekey(loginUserSession.getTransid(),loginUserSession.getLoginId());
			accessContext.setRsaPrivateKey(privateKey);
			//构造短信认证码
			AuthCode authCode = new AuthCode();
			authCode.setPhone(loginUserSession.getLoginId());
			authCode.setTransid(loginUserSession.getTransid());
			authCode.setAuthCode(loginUserSession.getAuthCode());
			
			int iRet= userLoginService.resetPasswrodByPhone(accessContext,countryCode,loginUserSession.getLoginId(),loginUserSession.getPassword(),loginUserSession,authCode);
			
			 processResult.setRetCode(iRet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	/**
	 * 修改密码
	 * @param request
	 * @param countryCode
	 * @param requestTokenBody
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/modifyPassword")
	public  ProcessResult modifyPassword(@PathVariable String countryCode,@RequestBody RequestModifyPassword requestModifyPassword) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {			
			
			String authKey = requestModifyPassword.getLoginIdType() +":" + requestModifyPassword.getLoginId();
			try {
				
				if(requestModifyPassword.getLoginIdType()==LoginUserSession.LoginIdType_phone)
				{
					authKey =requestModifyPassword.getLoginId();
					if(StringUtils.isEmpty(authKey))
					{
						authKey =requestModifyPassword.getPhone();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			AccessContext accessContext =new AccessContext();
			
			//RequestModifyPassword requestModifyPassword  =	JsonUtil.fromJson(requestTokenBody.getRequestBody(),RequestModifyPassword.class);
			accessContext.setTransid(requestModifyPassword.getTransid());
			PrivateKey privatekey = this.getPrivatekey(requestModifyPassword.getTransid(), authKey);
			accessContext.setRsaPrivateKey(privatekey);
		
			//accessContext.setLoginUserSession(loginUserSession);
			int iRet = -1;
			if(requestModifyPassword.getLoginIdType()==LoginUserSession.LoginIdType_phone)
			{
				iRet= userLoginService.modifyPasswrodByPhone(accessContext, requestModifyPassword.getPhone(), requestModifyPassword.getModifyKey(), requestModifyPassword.getNewPassword());
			}
			else if(requestModifyPassword.getLoginIdType()==LoginUserSession.LoginIdType_userName)
			{
				
				LoginUser loginUser = userLoginService.getLoginUserByUserName(requestModifyPassword.getLoginId());
				if(loginUser==null)
				{
					iRet = LoginServiceConst.RESULT_Error_UserNameNotExist;  
				}
				else
				{
					iRet= userLoginService.modifyPasswrodByUserId(accessContext, authKey, loginUser.getUserId(), requestModifyPassword.getModifyKey(), requestModifyPassword.getNewPassword());
				}
			}
			else
			{
				long userId = Long.parseLong(requestModifyPassword.getLoginId());
				iRet= userLoginService.modifyPasswrodByUserId(accessContext, authKey, userId, requestModifyPassword.getModifyKey(), requestModifyPassword.getNewPassword());
				
			}
				
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(accessContext.getLoginUserInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	
	
	
	@RequestMapping(method = RequestMethod.POST,value = "{countryCode}/getUserInfo")
	public  ProcessResult getUserInfo(@PathVariable String countryCode,@RequestBody SecurityUser securityUser) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			
			int iRet = this.userLoginService.getUserInfo(accessContext,securityUser.getPhone());
			processResult.setRetCode(iRet);
			if(LoginServiceConst.RESULT_Success==iRet)
			{
				processResult.setResponseInfo(accessContext.getObject());	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST,value = "{routerId}/getUserInfoById")
	public  ProcessResult getUserInfoById(@PathVariable String routerId,@RequestBody SecurityUser securityUser) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			
			int iRet = this.userLoginService.getUserInfoByUserId(accessContext,String.valueOf(securityUser.getUserId()));
			processResult.setRetCode(iRet);
			if(LoginServiceConst.RESULT_Success==iRet)
			{
				processResult.setResponseInfo(accessContext.getObject());	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "{countryCode}/modifyUserInfo")
	public  ProcessResult modifyUserInfo(@PathVariable String countryCode,@RequestBody SecurityUser securityUser) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			accessContext.setObject(securityUser);
			int iRet =-1;
			if(securityUser.getUserId()<=0)
			{
				iRet = this.userLoginService.modifyUserInfo(accessContext, securityUser.getPhone());
			}
			else
			{
				iRet = this.userLoginService.modifyUserInfo(accessContext, securityUser.getUserId());
				
			}
			processResult.setRetCode(iRet);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	@RequestMapping(method = RequestMethod.POST,value = "{countryCode}/beTeacher")
	public  ProcessResult beTeacher(@PathVariable String countryCode,@RequestBody SecurityUser securityUser) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			accessContext.setObject(securityUser);
			int iRet = this.userLoginService.modifyUserInfo(accessContext, securityUser.getPhone());
			processResult.setRetCode(iRet);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
}
