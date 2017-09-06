package com.company.security.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.company.security.Const.LoginServiceConst;
import com.company.security.Const.SecurityUserConst;
import com.company.security.domain.AccessContext;
import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.SecurityUser;
import com.company.security.service.IUserLoginService;
import com.company.security.service.SecurityUserCacheService;
import com.company.security.service.SecurityUserService;
import com.company.security.utils.SecurityUserAlgorithm;
@Service("userLoginService")
public class UserLoginServiceImpl implements IUserLoginService {
	@Autowired
	private SecurityUserCacheService securityUserCacheService;
	@Resource(name="userMainDbService")
	private SecurityUserService userMainDbService;
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
	 * 先从缓存中获取loginUser基本信息，如果不存在在从数据库获取
	 * @return --null 用户不存在
	 */
	protected LoginUser getLoginUser(String phone)
	{
		LoginUser loginUser = securityUserCacheService.getBInfoByPhone(phone);
		if(loginUser==null)
		{			
			//从数据库中获取信息，
			SecurityUser securityUser = userMainDbService.selectUserByPhone(phone);
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
	 * 校验密码信息
	 * @param accessContext
	 * @param countryCode
	 * @param phone
	 * @param password
	 * @return
	 */
	protected int checkPassword(AccessContext accessContext, String countryCode, String phone, String password)
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
			
			//如果密码不相等
			if(!loginUser.getPassword().equalsIgnoreCase(password))
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
	public int registerUserByPhone(AccessContext accessContext, String countryCode, String phone, String password) {
		// TODO Auto-generated method stub
		//获取用户名
		int iRet = LoginServiceConst.RESULT_Error_Fail;
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
		securityUser.setPassword(password);
		securityUser.setUserId(this.createUserId());
		accessContext.setLoginUserInfo(securityUser.getLoginUser());
		iRet = userMainDbService.registerUserByPhone(securityUser);
		return iRet;
	}
	@Override
	public int loginUserManual(AccessContext accessContext, String countryCode, String phone, String password) {
		// TODO Auto-generated method stub
		//根据电话号码获取loginUser信息
		int iRet = checkPassword(accessContext,countryCode,phone,password);
		if(iRet!=LoginServiceConst.RESULT_Success)
		{
			return iRet;
		}
		LoginUser  loginUser = accessContext.getLoginUserInfo();
		LoginUserSession loginUserSession = accessContext.getLoginUserSession();
		//构造token
		String token = getToken(loginUser);
		loginUserSession.setToken(token);
		//清除老的session
		clearOldsession(accessContext,loginUserSession);
		int durationSeconds = getSessionDurSedonds(loginUserSession.getLoginType());
		boolean bRet = securityUserCacheService.putSessionInfo(loginUserSession, durationSeconds);
		//
		if(!bRet)
		{
			iRet =  LoginServiceConst.RESULT_Error_putSession;
		}
		return iRet;
	}
	
	@Override
	public int resetPasswrodByPhone(AccessContext accessContext, String countryCode, String phone, String password) {
		// TODO Auto-generated method stub
		String encodeMd5 = SecurityUserAlgorithm.EncoderByMd5(transferUserKey,password);
		boolean bRet= userMainDbService.resetPasswordByPhone(phone, password, encodeMd5);
		if(bRet)
		{
			return LoginServiceConst.RESULT_Success;
		}
		else
		{
			return LoginServiceConst.RESULT_Error_Fail;
		}
	}
		
}
