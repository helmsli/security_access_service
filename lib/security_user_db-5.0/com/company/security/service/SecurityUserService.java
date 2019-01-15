package com.company.security.service;

import java.util.List;

import com.company.security.domain.SecurityUser;

public interface SecurityUserService {
	
	/**
	 * 注册用户，电话号码已经被认证过了，调用该函数
	 * @param security
	 * @return
	 */
	public int registerUserByPhone(SecurityUser securityUser);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public SecurityUser selectUserById(long userId);
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public SecurityUser selectUserByPhone(String phone);
	/**
	 * 
	 * @param email
	 * @return
	 */
	public SecurityUser selectUserByemail(String email);
	/**
	 * 
	 * @param idType
	 * @param idNo
	 * @return
	 */
	public SecurityUser selectUserByIdNo(int idType,String idNo);
	/**
	 * 
	 * @param userId
	 * @param pasword
	 * @param algorithm  -- 加密校验字，用于比较是否验证的身份是合法的。约定好的字符串+pasword
	 * @return
	
	public boolean  checkPassword(long userId,String pasword,String algorithm);
	*/
	/**
	 * 
	 * @param userId
	 * @param newpasword
	 * @param oldPassword
	 * @param algorithm -- 加密校验字， 约定好的字符串+newpasword
	 * @return
	 */
	public boolean  updatePassword(long userId,String newpasword,String oldPassword,String algorithm);
	/**
	 * 
	 * @param userId
	 * @param newpasword
	 * @param algorithm
	 * @return
	 */
	public boolean  resetPassword(long userId,String newpasword,String algorithm);
	
	/**
	 * 
	 * @param userId
	 * @param newpasword
	 * @param algorithm
	 * @return
	 */
	public boolean  resetPasswordByPhone(String phone,String newpasword,String algorithm);
	
	
	/**
	 * 
	 * @param securityUser
	 * @return
	 */
	public int updateUserBasicInfo(SecurityUser securityUser);
	/**
	 * 
	 * @param userId
	 * @param email
	 * @param status
	 * @return
	 */
	public int bindEmail(long userId,String email,int status);
	
	/**
	 * 解绑邮箱
	 * @param userId
	 * @param email
	 * @return
	 */
	public int unbindEmail(long userId,String email);
	
	/**
	 * 
	 * @param userId
	 * @param countryCode
	 * @param phone
	 * @param status
	 * @return
	 */
	public int bindPhone(long userId,String countryCode,String phone,int status);
	
	public int unbindPhone(long userId,String countryCode,String phone);
	
	/**
	 * 
	 * @param userId
	 * @param idType
	 * @param idNo
	 * @param status
	 * @return
	 */
	public int bindIdNo(long userId,int idType,String idNo,int status);
	
	/**
	 * 
	 * @param userId
	 * @param idType
	 * @param idNo
	 * @param status
	 * @return
	 */
	public int unbindIdNo(long userId,int idType,String idNo);
	
	/**
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int updateStatus(long userId,int status);
	
	/**
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public int updateRoles(long userId,String roles);
	
}
