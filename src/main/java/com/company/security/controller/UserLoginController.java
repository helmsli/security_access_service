package com.company.security.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.xinwei.nnl.common.domain.ProcessResult;

@Controller
@RestController
@RequestMapping("/{phoneNumber}/loginUser")
public class UserLoginController {
	
	@RequestMapping(method = RequestMethod.GET,value = "/{token}")
	ProcessResult getloginUsers(HttpServletRequest request,@PathVariable String phoneNumber, @PathVariable String token) {
		String oldPhoneNumber = (String)request.getSession().getAttribute("abcdefege");
		if(StringUtils.isEmpty(oldPhoneNumber))
		{
			request.getSession().setAttribute("abcdefege", phoneNumber);
		}
		else
		{
			request.getSession().setAttribute("abcdefege", phoneNumber);
			System.out.println("*******************old:(" + oldPhoneNumber + ") : new: " + phoneNumber);
				
		}
		return null;
		
	}
	
	
	/*
	 * @RequestMapping (value = "withDraw/{subsId}/{withDrawAmt}", method = { RequestMethod.GET, RequestMethod.POST })
	public ProcessResult withDraw(HttpServletRequest request, @PathVariable ("subsId") long subsId, @PathVariable ("withDrawAmt") double withDrawAmt)
	{
	 */
}
