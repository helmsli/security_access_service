package com.company.security.controller.rest;

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
import com.company.security.domain.AccessContext;
import com.company.security.domain.RequestLogin;
import com.company.security.domain.RequestModifyPassword;
import com.company.security.domain.RequestTokenBody;
import com.company.security.domain.sms.SmsContext;
import com.company.security.domain.sms.AuthCode;
import com.company.security.service.ISmsValidCodeService;
import com.company.security.service.IUserLoginService;
import com.xinwei.nnl.common.domain.ProcessResult;


@RestController
@RequestMapping("/user")
public class UserLoginController {
	
	@Resource(name="userLoginService")
	private IUserLoginService userLoginService;
	
	@Resource(name="smsValidCodeService")
	private ISmsValidCodeService smsValidCodeService;
	
	/**
	 * 认证码注册
	 * @param request
	 * @param countryCode
	 * @param requestTokenBody
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/registerByCode")
	public  ProcessResult registerUserByCode(HttpServletRequest request,@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			
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
	
	/**
	 * 密码登录
	 * @param countryCode
	 * @param loginUserSession -- phone,password
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value = "/{countryCode}/loginByPass")
	public  ProcessResult loginByPass(@PathVariable String countryCode,@RequestBody RequestLogin loginUserSession) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			accessContext.setTransid(loginUserSession.getTransid());
			accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.loginUserManual(accessContext, countryCode, loginUserSession.getLoginId(), loginUserSession.getPassword(),loginUserSession);
			processResult.setRetCode(iRet);
			loginUserSession.setPassword("");
			processResult.setResponseInfo(loginUserSession);
		} catch (Exception e) {
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
	public  ProcessResult getSmsValidCode(@RequestBody AuthCode AuthCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AuthCode smsValidCode =AuthCode;
			SmsContext smsContext  = new SmsContext();
			int iRet = smsValidCodeService.sendValidCodeBySms(smsContext, smsValidCode);
			processResult.setRetCode(iRet);
			smsContext.getSmsValidCode().setAuthCode("");
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
	public  ProcessResult getRandom(@RequestBody AuthCode AuthCode) {
		ProcessResult processResult =new ProcessResult();		
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AuthCode smsValidCode =AuthCode;
			SmsContext smsContext  = new SmsContext();
			int iRet = this.userLoginService.getRandom(smsContext, smsValidCode.getPhone());
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(smsContext.getSmsValidCode());
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
	@RequestMapping(method = RequestMethod.POST,value = "/modifyPassword")
	public  ProcessResult modifyPassword(HttpServletRequest request,@PathVariable String countryCode,@RequestBody RequestTokenBody requestTokenBody) {
		ProcessResult processResult =new ProcessResult();
		processResult.setRetCode(LoginServiceConst.RESULT_Error_Fail);
		try {
			AccessContext accessContext =new AccessContext();
			RequestModifyPassword requestModifyPassword  =(RequestModifyPassword)requestTokenBody.getRequestBody();
			//accessContext.setLoginUserSession(loginUserSession);
			int iRet= userLoginService.modifyPasswrodByPhone(accessContext, requestModifyPassword.getPhone(), requestModifyPassword.getModifyKey(), requestModifyPassword.getNewPassword());
			processResult.setRetCode(iRet);
			processResult.setResponseInfo(accessContext.getLoginUserInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResult;
	}
	
}
