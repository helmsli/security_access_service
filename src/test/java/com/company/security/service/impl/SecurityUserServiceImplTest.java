package com.company.security.service.impl;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.company.security.Const.SecurityUserConst;
import com.company.security.domain.LoginUser;
import com.company.security.domain.SecurityUser;
import com.company.security.domain.SecurityUserEmail;
import com.company.security.domain.SecurityUserPhone;
import com.company.security.mapper.SecurityUserEmailMapper;
import com.company.security.mapper.SecurityUserMapper;
import com.company.security.mapper.SecurityUserPhoneMapper;
import com.company.security.service.SecurityUserCacheService;
import com.company.security.service.SecurityUserService;
import com.company.security.utils.SecurityUserAlgorithm;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityUserServiceImplTest {

	@Autowired
	private  SecurityUserService securityUserService;
	
	@Autowired
	private SecurityUserMapper securityUserMapper;
	
	@Autowired
	private SecurityUserPhoneMapper securityUserPhoneMapper;
	
	@Autowired
	private SecurityUserEmailMapper securityUserEmailMapper;
	
	@Value("${hessian.transferUserKey}")  
	private String transferUserKey;
	
	@Autowired
	private SecurityUserCacheService securityUserCacheService;
	
	@Before
	public void setUp() throws Exception {
	}
	/**
	 * 创建默认的用户
	 * @return
	 */
	protected SecurityUser createDefaultSecurityUser()
	{
		SecurityUser securityUser = new SecurityUser();
		securityUser.setAvatar("avatar");
		Calendar now = Calendar.getInstance();
		now.set(now.MILLISECOND, 0);
		
		securityUser.setBirthday(now.getTime());
		securityUser.setBusinessaddress("setBusinessaddress");
		securityUser.setCreatesource("createsource");
		now.add(now.MINUTE, 20);
		securityUser.setCreatetime(now.getTime());
		securityUser.setDisplayname("displayname");
		securityUser.setEmail("email");
		securityUser.setEmailverified(securityUser.verified_Fail);
		securityUser.setExtdate("extdate");
		securityUser.setFirstname("firstname");
		securityUser.setHomeaddress("homeaddress");
		securityUser.setIdno("idno");
		securityUser.setIdtype(securityUser.IDTYPE_IdCard);
		securityUser.setIdverified(securityUser.verified_Fail);
		securityUser.setLastname("lastname");
		securityUser.setLoginname("loginname");
		securityUser.setPassword("password");
		securityUser.setPasswordext("passwordext");
		securityUser.setPhone("0018612131415");
		securityUser.setPhoneccode("0086");
		securityUser.setPhoneverified(securityUser.verified_Success);
		securityUser.setRoles("123,334");
		securityUser.setSex(securityUser.SEX_Male);
		securityUser.setStatus(securityUser.Status_OK);
		now.add(now.MINUTE, 20);
		securityUser.setUpdatetime(now.getTime());
		securityUser.setUserId(1000);	
		
		return securityUser;
	}
	
	/**
	 * 测试正常的电话号码注册流程
	 */
	@Test
	public void testRegisterUserByPhone() {
		
		//创建正常的号码
		SecurityUser securityUser = createDefaultSecurityUser();
		//删除数据库中的数据
		securityUserMapper.deleteSecurityUser(securityUser.getUserId());
		securityUserPhoneMapper.deleteUserPhone(securityUser.getPhone());
		//调用注册服务
		int ret = securityUserService.registerUserByPhone(securityUser);
		//返回数值需要是正确的
		assertEquals("registerUserByPhone error",SecurityUserConst.RESULT_SUCCESS,ret);
		//查询数据库中的用户数据
		List<SecurityUser> queryUserS = securityUserMapper.selectSecurityUser(securityUser.getUserId());
		//确保只有一条记录
		assertEquals("registerUserByPhone insert db ",1,queryUserS.size());
		SecurityUser queryDbUser = queryUserS.get(0);
		//确保查询处理的数据和插入的数据是相同的；
		assertEquals("registerUserByPhone db not equal",securityUser.toString(),queryDbUser.toString());
		//查询电话号码映射关系，确保映射关系中保存的UID是正确的。
		List<SecurityUserPhone> phoneIds = securityUserPhoneMapper.selectUserId(securityUser.getPhone());
		assertEquals("registerUserByPhone phone maps query error",1,phoneIds.size());
		SecurityUserPhone phoneId = phoneIds.get(0);
		assertEquals("registerUserByPhone phone maps query ids",securityUser.getUserId(),phoneId.getUserId());
		
		//再次注册应该返回电话已经存在
		 ret = securityUserService.registerUserByPhone(securityUser);
		//返回数值需要是正确的
		assertEquals("registerUserByPhone error",SecurityUserConst.RESULT_Error_PhoneExist,ret);
			
		//再次注册应该返回号码错误
		securityUser.setPhone("3445");
		 ret = securityUserService.registerUserByPhone(securityUser);
		//返回数值需要是正确的
		assertEquals("registerUserByPhone error",SecurityUserConst.RESULT_Error_PhoneError,ret);
	    		
		
	}
	
	/**
	 * 测试的更新状态流程
	 */
	@Test
	public void testUpdateStatus() {
		
		//创建正常的号码
		SecurityUser securityUser = createDefaultSecurityUser();
		LoginUser loginUser = securityUser.getLoginUser();
		securityUserCacheService.putBasicInfo(loginUser);
		LoginUser cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
		assertEquals("testBindEmail redis loginuser not equal",loginUser.toString(),cacheLoginUser.toString());
		
		//删除数据库中的数据
		securityUserMapper.deleteSecurityUser(securityUser.getUserId());
		securityUserPhoneMapper.deleteUserPhone(securityUser.getPhone());
		//调用注册服务
		int ret = securityUserService.registerUserByPhone(securityUser);
		//更新状态数值
		ret = securityUserService.updateStatus(securityUser.getUserId(), SecurityUser.Status_needVerified);
		assertEquals("testUpdateStatus result number error ",1,ret);
		
		List<SecurityUser> queryUserS = securityUserMapper.selectSecurityUser(securityUser.getUserId());
		SecurityUser queryDbUser = queryUserS.get(0);
		assertEquals("testUpdateStatus status not equal",SecurityUser.Status_needVerified,queryDbUser.getStatus());
		 cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
			assertTrue("testBindEmail redis loginuser not equal",cacheLoginUser==null);
	}

	
	@Test
	public void testBindEmail() {
		
		
		//创建正常的号码
		SecurityUser securityUser = createDefaultSecurityUser();
		LoginUser loginUser = securityUser.getLoginUser();
		securityUserCacheService.putBasicInfo(loginUser);
		LoginUser cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
		assertEquals("testBindEmail redis loginuser not equal",loginUser.toString(),cacheLoginUser.toString());
		//删除数据库中的数据
		securityUserMapper.deleteSecurityUser(securityUser.getUserId());
		securityUserPhoneMapper.deleteUserPhone(securityUser.getPhone());
		securityUser.setEmailverified(securityUser.verified_Fail);
		//调用注册服务
		int ret = securityUserService.registerUserByPhone(securityUser);
		//更新状态数值为验证成功
		ret = securityUserService.bindEmail(securityUser.getUserId(), securityUser.getEmail(), securityUser.verified_Success);
		assertEquals("testBindEmail result number error ",1,ret);
		
		List<SecurityUser> queryUserS = securityUserMapper.selectSecurityUser(securityUser.getUserId());
		SecurityUser queryDbUser = queryUserS.get(0);
		assertEquals("testBindEmail status not equal",SecurityUser.verified_Success,queryDbUser.getEmailverified());
		
		List<SecurityUserEmail> queryUserIds = securityUserEmailMapper.selectUserId(securityUser.getEmail());
		assertEquals("testBindEmail email maps number error ",1,queryUserIds.size());
		SecurityUserEmail queryUserId = queryUserIds.get(0);
		assertEquals("testBindEmail email userid error ",securityUser.getUserId(),queryUserId.getUserId());
		
		
		//更新状态数值
		securityUserEmailMapper.deleteUserEmail(securityUser.getEmail());
		ret = securityUserService.bindEmail(securityUser.getUserId(), securityUser.getEmail(), securityUser.verified_Fail);
		assertEquals("testBindEmail result number error ",1,ret);
				
		queryUserS = securityUserMapper.selectSecurityUser(securityUser.getUserId());
		queryDbUser = queryUserS.get(0);
		assertEquals("testBindEmail status not equal",SecurityUser.verified_Fail,queryDbUser.getEmailverified());
				
		queryUserIds = securityUserEmailMapper.selectUserId(securityUser.getEmail());
		assertEquals("testBindEmail email maps number error ",0,queryUserIds.size());
		
		 cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
		assertTrue("testBindEmail redis loginuser not equal",cacheLoginUser==null);
		
	}
	
	@Test
	public void testCheckPassword() {
		
		//创建正常的号码
		SecurityUser securityUser = createDefaultSecurityUser();
		LoginUser loginUser = securityUser.getLoginUser();
		securityUserCacheService.putBasicInfo(loginUser);
		LoginUser cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
		assertEquals("testBindEmail redis loginuser not equal",loginUser.toString(),cacheLoginUser.toString());
		
		//删除数据库中的数据
		securityUserMapper.deleteSecurityUser(securityUser.getUserId());
		securityUserPhoneMapper.deleteUserPhone(securityUser.getPhone());
		securityUser.setEmailverified(securityUser.verified_Fail);
		//调用注册服务
		int ret = securityUserService.registerUserByPhone(securityUser);
		//验证正确的密码
		String transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword());
		
		boolean bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		
		assertTrue("testCheckPassword result true error ",bret);
		//验证错误的密码
		bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword()+"a", "");
		assertFalse("testCheckPassword result false error ",bret);
		
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword()+"b");
		//验证错误的传输key
		bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		assertFalse("testCheckPassword transfer false error ",bret);
		
		/**
		 * 
		 */
		//验证错误的传输key
		String oldPassword = securityUser.getPassword();
		String newpasword = securityUser.getPassword()+"a";
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, oldPassword);
		bret =securityUserService.updatePassword(securityUser.getUserId(),newpasword , oldPassword, transferCrc);
		assertFalse("testCheckPassword updatePassword crc false error ",bret);
		//验证老密码不正确不允许修改
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, newpasword);
		bret =securityUserService.updatePassword(securityUser.getUserId(),newpasword , oldPassword+"a", transferCrc);
		assertFalse("testCheckPassword updatePassword oldpassword false error ",bret);
		
		//验证秘密错误没有修改数据库，确保最初的密码还能给校验通过
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword());
		bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		assertTrue("testCheckPassword result true error ",bret);
		//正确的修改密码
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, newpasword);
		bret =	securityUserService.updatePassword(securityUser.getUserId(),newpasword , oldPassword, transferCrc);
		assertTrue("testCheckPassword updatePassword success  error ",bret);
		//修改成功后，老密码不能登录
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword());
		bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		assertFalse("testCheckPassword old password can not check  error ",bret);
		//修改成功后，新密码可以登录
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, newpasword);
		bret = securityUserService.checkPassword(securityUser.getUserId(), newpasword, transferCrc);
		assertTrue("testCheckPassword old password can not check  error ",bret);
		
		//重置为老密码
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword());
		bret = securityUserService.resetPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		//重置成功后，老密码能登录
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, securityUser.getPassword());
		bret = securityUserService.checkPassword(securityUser.getUserId(), securityUser.getPassword(), transferCrc);
		assertTrue("testCheckPassword reset old password can not check  error ",bret);
		//重置成功后，新密码不能登录
		transferCrc = SecurityUserAlgorithm.EncoderByMd5(this.transferUserKey, newpasword);
		bret = securityUserService.checkPassword(securityUser.getUserId(), newpasword, transferCrc);
		assertFalse("testCheckPassword reset old password can not check  error ",bret);
		 cacheLoginUser = securityUserCacheService.getBasicInfo(securityUser.getUserId());
		assertTrue("testBindEmail redis loginuser not equal",cacheLoginUser==null);
			
	}

}
