package com.company.security.service.impl;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.company.security.Const.LoginServiceConst;
import com.company.security.Const.SecurityUserConst;
import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.SecurityUser;
import com.company.security.domain.sms.AuthCode;
import com.company.security.domain.sms.SmsContext;
import com.company.security.service.ISmsValidCodeService;
import com.company.security.service.IUserLoginService;
import com.company.security.service.SecurityUserCacheService;
import com.company.security.service.SecurityUserService;
import com.company.security.utils.RSAUtils;
import com.company.security.utils.SecurityUserAlgorithm;
@Service("userLoginService")
public class UserLoginServiceImpl implements IUserLoginService {
	@Resource(name="securityUserCacheService")
	private SecurityUserCacheService securityUserCacheService;
	@Resource(name="userMainDbService")
	private SecurityUserService userMainDbService;
	//读数据库
	@Resource(name="userReadDbService")
	private SecurityUserService userReadDbService;
	/**
	 * 短信认证服务
	 */
	@Resource(name="smsValidCodeService")
	private ISmsValidCodeService smsValidCodeService;
	
	@Value("${db.dbUserkey}")  
	private String dbUserKey;	
	@Value("${hessian.transferUserKey}")  
	private String transferUserKey;
	
	@Value("${session.webDurSeconds}")  
	private int webDurSeconds;
	
	@Value("${session.mobileDurSeconds}")  
	private int mobileDurSeconds;
	
	@Value("${session.padDurSeconds}")  
	private int padDurSeconds;
	
	@Value("${session.pcDurSeconds}")  
	private int pcDurSeconds;
	
	@Value("${userid.localPrefix}")  
	private int localUseridPrefix;
	
	/**
	 * 新申请的UserID的起始id
	 */
	private long startNewuserId=0;
	
	/**
	 * 本地的Rsakey生成器
	 */
	private KeyPair rsaKeyPair = RSAUtils.generateKeyPair();
	
	/**
	 * 本地初始化的userid，如果cache出错，会使用这个变量
	 */
	private long localstartNewuserId=0;
	
	/**
	 * 剩余的uid个数；
	 */
	private int remainUseridNumber=0;
	
	/**
	 * 获取token
	 * @param loginUser
	 * @return
	 */
	protected String getToken(LoginUser loginUser)
	{
		long currentTime = System.currentTimeMillis()-1504369881000l;
		StringBuilder str = new StringBuilder();
		str.append(loginUser.getUserId());
		str.append("*");
		str.append(currentTime);
		return str.toString();
	}
	
	/**
	 * 获取登录信息的session
	 * @param loginType
	 * @return
	 */
	protected int getSessionDurSedonds(int loginType)
	{
		if(LoginUserSession.loginType_mobile==loginType)
		{
			return this.mobileDurSeconds;
		}
		if(LoginUserSession.loginType_pad==loginType)
		{
			return this.padDurSeconds;
		}
		if(LoginUserSession.loginType_web==loginType)
		{
			return this.webDurSeconds;
		}
		if(LoginUserSession.loginType_pc==loginType)
		{
			return this.pcDurSeconds;
		}
		return this.webDurSeconds;
	}
	
	/**
	 * 获取主库或者从库的读写记录
	 * @param phone
	 * @return
	 */
	protected SecurityUserService getSecurityService(String phone)
	{
		long lastModify = this.securityUserCacheService.getLastModifyTime(phone);
		if(lastModify==0)
		{
			return this.userReadDbService;
		}
		else
		{
			return userMainDbService;
		}
	}
	
	/**
	 * 先从缓存中获取loginUser基本信息，如果不存在在从数据库获取
	 * @return --null 用户不存在
	 */
	protected LoginUser getLoginUser(String phone)
	{
		LoginUser loginUser = securityUserCacheService.getBInfoByPhone(phone);
		if(loginUser==null)
		{		
			//从数据库中获取信息，
			//如果最近修改过
			SecurityUserService securityUserService = this.getSecurityService(phone);
			SecurityUser securityUser = securityUserService.selectUserByPhone(phone);
			//如果数据库中不存在
			if(securityUser==null)
			{
				return null;
			}
			else
			{
				loginUser = securityUser.getLoginUser();
			}
		}
		return loginUser;
	}
	
	/**
	 * 将数据库的密码和随机数加密，以方便和客户端比较
	 * @param accessContext
	 * @param phone
	 * @param dbPassword
	 * @return
	 */
	protected String getCorrentPassword(AccessContext accessContext, String phone,String dbPassword)
	{
		
		//不用MD5加密，仅仅用随机数加密
		//String random = getRandom(phone,accessContext.getTransid());
		//String correctPassword = SecurityUserAlgorithm.EncoderByMd5(random, dbPassword);
		return dbPassword;
	}
	/**
	 * 从客户端发送的密码中获取正确的密码
	 * @param accessContext
	 * @param phone
	 * @param dbPassword
	 * @return
	 */
	protected String getPasswordFromRsa(AccessContext accessContext, String phone,String clientPassword)
	{
		
		String random = getRandom(phone,accessContext.getTransid());
		PrivateKey privateKey = accessContext.getRsaPrivateKey();
		String allClientPassword = RSAUtils.decrypt(clientPassword, privateKey);
		if(allClientPassword.startsWith(random))
		{
			String initClientPassword =  allClientPassword.substring(random.length());
			String correctPassword = SecurityUserAlgorithm.EncoderByMd5(dbUserKey, initClientPassword);
		    return correctPassword;
		}
		else
		{
			return allClientPassword;
		}
	}
	
	/**
	 * 校验密码信息
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	protected int checkPassword(AccessContext accessContext, String phone, String password)
	{
		int bRet = LoginServiceConst.RESULT_Error_PasswordError;
		try {
			LoginUserSession loginUserSession =accessContext.getLoginUserSession();
			LoginUser loginUser = getLoginUser(phone);
			if(loginUser==null)
			{
				bRet = SecurityUserConst.RESULT_Error_PhoneExist;
				return bRet;
			}
			String correctPassword = getCorrentPassword(accessContext,phone,loginUser.getPassword());
			String clientPassword  = getPasswordFromRsa(accessContext,phone,password);
			//如果密码不相等
			if(!correctPassword.equalsIgnoreCase(clientPassword))
			{
				return LoginServiceConst.RESULT_Error_PasswordError;
			}
			
			//如果密码正确，并且内存中不存在，先放入内存
			loginUserSession.setUserId(loginUser.getUserId());
			if(bRet==SecurityUserConst.RESULT_Error_PhoneExist)
			{
				securityUserCacheService.putBasicInfo(loginUser);
			}
			accessContext.setLoginUserInfo(loginUser);
			return LoginServiceConst.RESULT_Success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bRet;
	}
	
	/**
	 * 清除老的session信息
	 * @param loginUserSession
	 */
	protected void clearOldsession(AccessContext accessContext,LoginUserSession loginUserSession)
	{
		try {
			//获取老的Token，将老的Token设置为失效
					LoginUserSession oldLoginSession = securityUserCacheService.getSessionInfo(loginUserSession.getLoginType(), loginUserSession.getUserId());
					if(oldLoginSession!=null)
					{
						securityUserCacheService.delSessionAccessTime(oldLoginSession.getToken());
					}
					accessContext.setOldUserSession(oldLoginSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 如果cache出错，创建本地的userid
	 * @return
	 */
	protected  long createLocalUserId()
	{
		/**
		 * long 最大19为，因此如果是自己生成的UID
		 * 系统生成的最大位数为11为，本地生成的最大为14
		 * 本地生成的id为注入的前缀左移11位，在加上当前时间的毫秒数。
		 * 1年的毫秒数 31536000 
		 */
		//获取1970年以来的秒数，14位   3153600000000  1504369881000
		if(localstartNewuserId==0)
		{
			//当前时间减去1970年的基准时间
			localstartNewuserId =  System.currentTimeMillis()-1504369881000l;
			//确保本地生成的为14位
			if(localUseridPrefix<100)
			{
				localUseridPrefix = localUseridPrefix + 100;
			}
			localstartNewuserId = this.localUseridPrefix * 100000000000l+localstartNewuserId;
		}
		
		//前缀时间+本地开始uid+偏移量
		long endUserid =  localstartNewuserId++;
		return endUserid;
		
	}
	
	/**
	 * 
	 * @return
	 */
	protected synchronized long createUserId()
	{
		long newUserid = 0;
		//如果本地还剩余UID，直接使用
		if(this.remainUseridNumber>0)
		{
			remainUseridNumber--;
			newUserid= ++this.startNewuserId;
			return newUserid;
		}
		else
		{
			//从缓存中申请UID，每次申请1000个
			int queryNums = 1000;
			try {
				startNewuserId = this.securityUserCacheService.createUserId(queryNums);
				//从缓存中申请成功
				if(startNewuserId>0)
				{
					newUserid=startNewuserId - queryNums;
					startNewuserId=startNewuserId - queryNums + 1;
					remainUseridNumber=queryNums - 1;
				}	
				//从缓存中申请失败，在本地申请一个
				else
				{
					newUserid = createLocalUserId();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//如果从缓存中申请失败，在本地申请一个
				newUserid = createLocalUserId();				
			}
			
		}
		return newUserid;
	}


	@Override
	public int registerUserByCode(AccessContext accessContext,String countryCode,String phone,String password,LoginUserSession loginUserSession,AuthCode validCode) {
		// TODO Auto-generated method stub
		//获取用户名
		int iRet = LoginServiceConst.RESULT_Error_Fail;
		//校验短信认证码
		SmsContext smsContext = new SmsContext();
		iRet = smsValidCodeService.checkValidCodeBySms(smsContext, validCode);
		if(LoginServiceConst.RESULT_Success!=iRet)
		{
			return iRet;
		}
		//注册用户
		LoginUser  loginUser = getLoginUser(phone);
		if(loginUser!=null)
		{
			accessContext.setLoginUserInfo(loginUser);
			return LoginServiceConst.RESULT_Error_PhoneHaveRegister;
		}
		//更新数据库信息
		SecurityUser securityUser =new SecurityUser();
		securityUser.setPhone(phone);
		securityUser.setPhoneccode(countryCode);
		String clientPassword = this.getPasswordFromRsa(accessContext, phone, password);
		securityUser.setPassword(clientPassword);
		securityUser.setUserId(this.createUserId());
		securityUser.setPhoneverified(securityUser.verified_Success);
		accessContext.setLoginUserInfo(securityUser.getLoginUser());
		
		securityUser.setCreatetime(Calendar.getInstance().getTime());
		iRet = userMainDbService.registerUserByPhone(securityUser);
		return iRet;
	}
	
	/**
	 * 不需要验证，验证成功后基础的登录流程
	 * @param accessContext
	 * @param loginUser
	 * @param loginUserSession
	 * @return
	 */
	protected int baseLogin(AccessContext accessContext,LoginUser  loginUser,LoginUserSession loginUserSession)
	{
		int iRet = LoginServiceConst.RESULT_Error_Fail;
		accessContext.setLoginUserInfo(loginUser);
		accessContext.setLoginUserSession(loginUserSession);
		//构造token
		String token = getToken(loginUser);
		loginUserSession.setToken(token);
		//清除老的session
		clearOldsession(accessContext,loginUserSession);
		int durationSeconds = getSessionDurSedonds(loginUserSession.getLoginType());
		boolean bRet = securityUserCacheService.putSessionInfo(loginUserSession, loginUser,durationSeconds);
		//
		if(!bRet)
		{
			iRet =  LoginServiceConst.RESULT_Error_putSession;
		}
		else
		{
			iRet =  LoginServiceConst.RESULT_Success;
		}
		return iRet;
	}
	
	@Override
	public int loginUserManual(AccessContext accessContext, String countryCode, String phone, String password,LoginUserSession loginUserSession) {
		// TODO Auto-generated method stub
		//根据电话号码获取loginUser信息
		int iRet = LoginServiceConst.RESULT_Error_Fail;
		try {
			accessContext.setLoginUserSession(loginUserSession);
			 iRet = checkPassword(accessContext,phone,password);
			if(iRet!=LoginServiceConst.RESULT_Success)
			{
				return iRet;
			}					
			LoginUser  loginUser = accessContext.getLoginUserInfo();
			return baseLogin(accessContext,loginUser,loginUserSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(iRet == LoginServiceConst.RESULT_Success)
			{
				iRet = LoginServiceConst.RESULT_Error_Fail;
			}
		}
		return iRet;
		
	}
	@Override
	public int loginUserBySmsCode(AccessContext accessContext,String countryCode,String phone,LoginUserSession loginUserSession,AuthCode validCode) {
		// TODO Auto-generated method stub
		//验证短信认证码
		SmsContext smsContext= new SmsContext();
		int iRet = this.smsValidCodeService.checkValidCodeBySms(smsContext, validCode);
		if(LoginServiceConst.RESULT_Success!=iRet)
		{
			return iRet;
		}
		accessContext.setLoginUserSession(loginUserSession);
		LoginUser loginUser = this.getLoginUser(phone);
		//电话号码不存在
		if(loginUser==null)
		{
			return SecurityUserConst.RESULT_Error_PhoneExist;
		}
		accessContext.setLoginUserInfo(loginUser);
		return this.baseLogin(accessContext, loginUser, loginUserSession);
		
	}
	@Override
	public int loginUserAuto(AccessContext accessContext, String countryCode, String phone, String password) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int resetPasswrodByPhone(AccessContext accessContext,String countryCode,String phone,String password,LoginUserSession loginUserSession,AuthCode validCode) {
		// TODO Auto-generated method stub
		
		SmsContext smsContext  = new SmsContext();
		
		int iRet = smsValidCodeService.checkValidCodeBySms(smsContext, validCode);
		if(iRet == LoginServiceConst.RESULT_Success)
		{	
			String clientPassword = this.getPasswordFromRsa(accessContext, phone, password);
			String encodeMd5 = SecurityUserAlgorithm.EncoderByMd5(transferUserKey,clientPassword);
			boolean bRet= userMainDbService.resetPasswordByPhone(phone, clientPassword, encodeMd5);
			if(bRet)
			{
				return LoginServiceConst.RESULT_Success;
			}
			else
			{
				return LoginServiceConst.RESULT_Error_Fail;
			}
		}
		return iRet;
		
	}
	
	@Override
	public int modifyPasswrodByPhone(AccessContext accessContext, String phone, String oldPassword,String newPassword) {
		// TODO Auto-generated method stub
		LoginUser loginUser = getLoginUser(phone);
		if(loginUser!=null)
		{
			String clientNewPassword = this.getPasswordFromRsa(accessContext, phone, newPassword);
			String clientOldPassword = this.getPasswordFromRsa(accessContext, phone, oldPassword);
			String encodeMd5 = SecurityUserAlgorithm.EncoderByMd5(transferUserKey,clientNewPassword);
			boolean bRet= userMainDbService.updatePassword(loginUser.getUserId(), clientNewPassword, clientOldPassword, encodeMd5);
			if(bRet)
			{
				return LoginServiceConst.RESULT_Success;
			}
			else
			{
				return LoginServiceConst.RESULT_Error_Fail;
			}
		}
		return SecurityUserConst.RESULT_Error_PhoneError;
	}

	public SecurityUserService getUserMainDbService() {
		return userMainDbService;
	}

	public void setUserMainDbService(SecurityUserService userMainDbService) {
		this.userMainDbService = userMainDbService;
	}

	public SecurityUserService getUserReadDbService() {
		return userReadDbService;
	}

	public void setUserReadDbService(SecurityUserService userReadDbService) {
		this.userReadDbService = userReadDbService;
	}

	@Override
	public int createRandom(SmsContext smsContext,String phone) 
	 {
		// TODO Auto-generated method stub
		String retStr=this.securityUserCacheService.getTrandsId(phone);
		String[] transInfo = StringUtils.split(retStr, SecurityUserCacheKeyService.Key_prefix_Split);
		if(transInfo.length==2)
		{
			AuthCode authCode = smsContext.getSmsValidCode();
			authCode.setTransid(transInfo[0]);
			authCode.setRandom(transInfo[1]);
			//smsContext.setSmsValidCode(authCode);
			return 0;
		}
		return -1;
	}

	

	@Override
	public String getRandom(String phone, String transid) {
		// TODO Auto-generated method stub
		String random = this.securityUserCacheService.getRandomByTransid(phone, transid);
		if(StringUtils.isEmpty(random))
		{
			random="";
		}
		return random;
	}

	@Override
	public KeyPair getRsaInfo(String phone) {
		// TODO Auto-generated method stub	     
		return this.rsaKeyPair;
	}

	
			
}
