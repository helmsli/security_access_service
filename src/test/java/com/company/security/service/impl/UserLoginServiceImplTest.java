package com.company.security.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.SecurityUser;
import com.company.security.service.IUserLoginService;
import com.company.security.service.SecurityUserService;
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserLoginServiceImplTest  {

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
		securityUser.setPhone("121121");
		securityUser.setPassword("3333");
		when(userMainDbService.selectUserByPhone("phone")).thenReturn(securityUser);
		when(userReadDbService.selectUserByPhone("phone")).thenReturn(securityUser);
		//fail("Not yet implemented");
		AccessContext accessContext=new AccessContext();
		String countryCode = "";
		String phone ="";
		String password ="";
		LoginUserSession loginUserSession = new LoginUserSession();
		userLoginService.loginUserManual(accessContext, "0086","phone12", "abc", loginUserSession);
		
	}

}
