package com.company.security.service.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.company.security.domain.AccessContext;
import com.company.security.service.IUserLoginService;

public class UserLoginServiceImplTest extends UserLoginServiceImpl {

	@Resource(name="userLoginService") 
	private IUserLoginService userLoginService;
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLoginUserManual() {
		//fail("Not yet implemented");
		AccessContext accessContext=new AccessContext();
		String countryCode = "";
		String phone ="";
		String password ="";
		userLoginService.loginUserManual(accessContext, countryCode, phone, password);
		
	}

}
