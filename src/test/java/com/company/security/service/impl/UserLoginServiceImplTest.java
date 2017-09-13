package com.company.security.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.security.Const.LoginServiceConst;
import com.company.security.Const.SecurityUserConst;
import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.SecurityUser;
import com.company.security.domain.sms.AuthCode;
import com.company.security.domain.sms.SmsContext;
import com.company.security.service.ISmsValidCodeService;
import com.company.security.service.IUserLoginService;
import com.company.security.service.SecurityUserService;
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserLoginServiceImplTest  {

	@Resource(name="smsValidCodeService")
	private ISmsValidCodeService smsValidCodeService;
	/**
	 * 构造数据库测试接口
	 */
	@Mock(name ="userMainDbService")
	private SecurityUserService userMainDbService;
	
	@Mock(name ="userReadDbService")
	private SecurityUserService userReadDbService;
	
	@InjectMocks
	@Resource(name="userLoginService") 
	private IUserLoginService userLoginService;
	@Before
	public void setUp() throws Exception {
		 MockitoAnnotations.initMocks(this); 	
		
	}
	/*
	@Before
	public void initMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}
	*/
	@Test
	public void testLoginUserManual() {
		SecurityUser securityUser = new SecurityUser();
		securityUser.setPhone("008618612341233");
		securityUser.setPassword("3333");
		securityUser.setUserId(1234545l);
		when(userMainDbService.selectUserByPhone("008618612341233")).thenReturn(securityUser);
		when(userReadDbService.selectUserByPhone("008618612341233")).thenReturn(securityUser);
		//fail("Not yet implemented");
		AccessContext accessContext=new AccessContext();
		String countryCode = "";
		String phone ="";
		String password ="";
		//测试密码错的情况
		LoginUserSession loginUserSession = new LoginUserSession();
		int iRet = userLoginService.loginUserManual(accessContext, "0086","008618612341233", securityUser.getPassword()+"122", loginUserSession);
		assertEquals("testLoginUserManual password not equal",LoginServiceConst.RESULT_Error_PasswordError,iRet);
		//测试正确的登录成功
		iRet = userLoginService.loginUserManual(accessContext, "0086","008618612341233", securityUser.getPassword(), loginUserSession);
		assertEquals("testLoginUserManual password not equal",LoginServiceConst.RESULT_Success,iRet);
		//测试用户不存在；
		 iRet = userLoginService.loginUserManual(accessContext, "0086",securityUser.getPhone()+"a", securityUser.getPassword()+"122", loginUserSession);
		assertEquals("testLoginUserManual password not equal",SecurityUserConst.RESULT_Error_PhoneExist,iRet);
		
	}
	@Test
	public void testregisterUserByCode()
	{
		
		
		
		String countryCode = "0086";
		String phone="008613912345678";
		String password = "12345678";
		SecurityUser securityUser = new SecurityUser();
		securityUser.setPhone(phone);
		securityUser.setPassword(password);
		securityUser.setUserId(1234545l);
		when(userMainDbService.selectUserByPhone(Mockito.anyString())).thenReturn(null);
		when(userReadDbService.selectUserByPhone(phone)).thenReturn(null);
		when(userMainDbService.registerUserByPhone(Mockito.any())).thenReturn(LoginServiceConst.RESULT_Success);
		
		LoginUserSession loginUserSession = new LoginUserSession();
		AuthCode validCode = new AuthCode();
		validCode.setPhone(phone);
		SmsContext smsContext = new SmsContext();
		this.smsValidCodeService.sendValidCodeBySms(smsContext, validCode);
		validCode.setAuthCode(smsContext.getAuthCode());
		AccessContext accessContext = new AccessContext();
		int iRet =userLoginService.registerUserByCode(accessContext, countryCode, phone, password, loginUserSession, validCode);
		assertEquals("testregisterUserByCode  not equal",SecurityUserConst.RESULT_SUCCESS,iRet);
		
	}
	

}
