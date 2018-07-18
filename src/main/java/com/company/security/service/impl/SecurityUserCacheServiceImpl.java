package com.company.security.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.security.Const.SessionKeyConst;
import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.service.SecurityUserCacheService;
import com.company.security.utils.RSAUtils;
import com.xinwei.nnl.common.util.JsonUtil;
@Service("securityUserCacheService")
public class SecurityUserCacheServiceImpl extends SecurityUserCacheKeyService implements SecurityUserCacheService,InitializingBean {
	
	@Resource (name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	
	@Resource(name="redisLockService")
	private RedisLockServiceImpl redisLockService;
	
	@Value("${user.synReadDbSeconds}")  
	private int synReadDbSeconds;
	
	@Value("${server.idnode}")  
	private String serverNode;
	
	@Value("${user.cacheExpireHours:36}")  
	private int userCacheExpireHours;
	
	 private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * token在内存中失效的天数,仅仅时内存中保留的天数，不是token失效的天数
	 */
	protected int tokenExpiredDays = 5;
	
	/**
	 * 将基本信息放入cache，不加锁
	 * @param loginUser
	 * @return
	 */
	protected boolean cacheBasicInfoNolock(LoginUser loginUser)
	{
		this.cleanAllUserCache(loginUser.getUserId());
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		try {
			String userKey = this.getLoginUserkey(loginUser.getUserId());
			opsForValue.set(userKey, JsonUtil.toJson(loginUser),userCacheExpireHours,TimeUnit.HOURS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String emailkey = this.getEmailKey(loginUser.getEmail());
			opsForValue.set(emailkey, new Long(loginUser.getUserId()),userCacheExpireHours,TimeUnit.HOURS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String phoneKey = this.getPhoneKey(loginUser.getPhone());
			opsForValue.set(phoneKey, new Long(loginUser.getUserId()),userCacheExpireHours,TimeUnit.HOURS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean putBasicInfo(LoginUser loginUser) {
		String lockKey = this.getLockkey(loginUser.getUserId());
		String transTime = String.valueOf(System.currentTimeMillis());
		boolean isLock = getLock(lockKey, transTime);
		
		try {
			// TODO Auto-generated method stub
			if(isLock)
			{
				cacheBasicInfoNolock(loginUser);
					
			}
		} finally {
			if (isLock) {
				try {
					this.redisLockService.releaseUserTransLock(lockKey, transTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public LoginUser getBasicInfo(long userId) {
		// TODO Auto-generated method stub
		
		try {
			String userKey = this.getLoginUserkey(userId);
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			String jsonStr = (String)opsForValue.get(userKey);;
			LoginUser loginUser = JsonUtil.fromJson(jsonStr, LoginUser.class);
			return loginUser;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public LoginUser getBInfoByPhone(String phone) {
		// TODO Auto-generated method stub
		try {
			String phoneKey = this.getPhoneKey(phone);
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			
			Long  userid = (Long)opsForValue.get(phoneKey);
			if(userid!=null)
			{
				return getBasicInfo(userid.longValue());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public LoginUser getBInfoByEmail(String email) {
		// TODO Auto-generated method stub
		try {
			String emailkey = this.getEmailKey(email);
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			
			Long  userid = (Long)opsForValue.get(emailkey);
			return getBasicInfo(userid.longValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean removeBasinInfo(long userId) {
		// TODO Auto-generated method stub
		try {
			String key = this.getLoginUserkey(userId);
			redisTemplate.delete(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public long getLastModifyTime(String phone) {
		String modifyKey = this.getLastModifyKeyPhone(phone);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String lastModifyTime = (String)opsForValue.get(modifyKey);
		try {
			if(!StringUtils.isEmpty(lastModifyTime))
			{
				return Long.parseLong(lastModifyTime);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return 0;
	}	
	
	@Override
	public long getLastModifyTime(long userId) {
		// TODO Auto-generated method stub
		String modifyKey = this.getLastModifyKey(userId);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String lastModifyTime = (String)opsForValue.get(modifyKey);
		try {
			if(!StringUtils.isEmpty(lastModifyTime))
			{
				return Long.parseLong(lastModifyTime);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return 0;
	}
	/**
	 * 清除所有缓存
	 * @param userId
	 */
	protected void cleanAllUserCache(long userId)
	{
		try {
			String key = this.getLoginUserkey(userId);
			LoginUser loginUser = getBasicInfo(userId);
			if(loginUser!=null)
			{
				String childKey = this.getEmailKey(loginUser.getEmail());
				redisTemplate.delete(childKey);
				
				childKey = this.getPhoneKey(loginUser.getPhone());
				redisTemplate.delete(childKey);
			}
			redisTemplate.delete(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public boolean putLastModifyTime(LoginUser loginUser, long lastModifyTime) {
		// TODO Auto-generated method stub
		String lockKey = this.getLockkey(loginUser.getUserId());
		String transTime = String.valueOf(System.currentTimeMillis());
		boolean isLock = getLock(lockKey,transTime);
		try {
			if (isLock) {
               String modifyKey = this.getLastModifyKey(loginUser.getUserId());
               String modifyKeyphone = this.getLastModifyKeyPhone(loginUser.getPhone());
               
               ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
               opsForValue.set(modifyKey, String.valueOf(lastModifyTime),synReadDbSeconds,TimeUnit.SECONDS);               
               opsForValue.set(modifyKeyphone, String.valueOf(lastModifyTime),synReadDbSeconds,TimeUnit.SECONDS);               
   			   cleanAllUserCache(loginUser.getUserId());
			} 
		} finally {
			if (isLock) {
				try {
					this.redisLockService.releaseUserTransLock(lockKey, transTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param key
	 * @param accesskey
	 * @return
	 */
	protected boolean getLock(String key,String accesskey)
	{
		return redisLockService.getUserTransLock(key, accesskey, 20000, 1000, 20000);				 
	}

	/**
	 * 
	 * @param loginUserSession
	 * @param forceUpdate
	 * @return
	 */
	protected boolean refreshSessionInfo(LoginUserSession loginUserSession,int duartionSeconds)
	{
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		//按照登录的渠道类型和用户ID构造用户登录的具体设备信息
		String tokenkey = this.getTokenKey(loginUserSession.getLoginType(), loginUserSession.getUserId());
		//所有的登录设备信息保留一个固定的时间
		opsForValue.set(tokenkey, JsonUtil.toJson(loginUserSession),tokenExpiredDays,TimeUnit.DAYS);
		//设置token信息
		this.setSessionAccessTime(loginUserSession.getToken(), System.currentTimeMillis(), duartionSeconds);		
		return true;
	}
	@Override
	public boolean putSessionInfo(LoginUserSession loginUserSession,LoginUser loginUser,int duartionSeconds) {
		String lockKey = this.getLockkey(loginUserSession.getUserId());
		String transTime = String.valueOf(System.currentTimeMillis());
		
		boolean isLock = getLock(lockKey, transTime);
		
		try {
			// TODO Auto-generated method stub
			if(isLock)
			{
				refreshSessionInfo(loginUserSession,duartionSeconds);
				cacheBasicInfoNolock(loginUser);
			}
			return isLock;
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally {
			if (isLock) {
				try {
					this.redisLockService.releaseUserTransLock(lockKey, transTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}

	@Override
	public LoginUserSession getSessionInfo(int loginType, long userId) {
		// TODO Auto-generated method stub
		String tokenkey = this.getTokenKey(loginType, userId);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String jsonStr = (String)opsForValue.get(tokenkey);
		return (LoginUserSession)(JsonUtil.fromJson(jsonStr, LoginUserSession.class));
	}

	
	public long getSessionAccessTime(String token) {
		// TODO Auto-generated method stub
		String accessKey  =this.getTokenAccessKey(token);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String accessTime = (String)(opsForValue.get(accessKey)); 
		if(StringUtils.isEmpty(accessTime))
		{
			return 0;
		}
		return Long.parseLong(accessTime);
		
	}

	
	public boolean setSessionAccessTime(String token, long accessTime,int duartionSeconds) {
		//获取tokne的ken
		String accessKey  =this.getTokenAccessKey(token);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		logger.debug("set session:" + accessKey + ":" + String.valueOf(accessTime));
		//判断token是否有效
		opsForValue.set(accessKey, String.valueOf(accessTime),duartionSeconds,TimeUnit.SECONDS);
		return true;
	}

	
	public boolean delSessionAccessTime(String token) {
		// TODO Auto-generated method stub
		String accessKey  =this.getTokenAccessKey(token);
		redisTemplate.delete(accessKey);
		return true;
	}

	@Override
	public long createUserId(int numbers) {
		// TODO Auto-generated method stub
		String key = this.getCreateUserIdkey();
		
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		long retValue= opsForValue.increment(key, numbers);
		if(retValue<200000)
		{
			String lockSession="";
			String lockKey ="userIdSet__lock";
			try
			{
				 lockSession = this.redisLockService.getCommonTransLock(lockKey, 5000, 5000);
				if(lockSession!=null&&lockSession!="")
				{
					long ll = System.currentTimeMillis() - 1523757177694L;
					if(ll<=0)
					{
						ll =0;
					}
					ll = ll/1000;
					return retValue = opsForValue.increment(key, ll);
				}
				return 0L;
			}
			catch(Exception e)
			{
				return 0L;
			}
			finally
			{
				redisLockService.releaseUserTransLock(lockKey, lockSession);
			}
		}
		else
		{
			return retValue;
		}
		
	}


	@Override
	public String getTrandsId(String loginId) {
		// TODO Auto-generated method stub
		String transid = getTransId();
		String random = getRandom4();
		String key = getRandomkey(loginId,transid);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();		
		opsForValue.set(key, random,300,TimeUnit.SECONDS);		
		return transid+SecurityUserCacheKeyService.Key_prefix_Split+random;
	}
	@Override
	public String getRandomByTransid(String loginId,String transid)
	{
		String key = getRandomkey(loginId,transid);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();		
		String random = (String) opsForValue.get(key);	
		return random;
	}
	/**
	 * 获取transid
	 * @return
	 */
	public String getTransId()
	{
		long transId =  System.currentTimeMillis()-1504369881000l;
		return serverNode + transId+"*"+getRandom4();
	}
	
	/**
	 * 获取4位数字的随机数
	 * @return
	 */
	protected String getRandom4()
	{
		int mobile_code = (int)((Math.random()*9+1)*1000);
		if(mobile_code>9999)
		{
			String ret = String.valueOf(mobile_code);
			return ret.substring(0, 3);
		}
		return String.valueOf(mobile_code);
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		if(this.serverNode.length()!=RSAUtils.Length_ServeridNode)
		{
			throw new Exception("the length of server.idnode should be " + RSAUtils.Length_ServeridNode);
		}
		
	}
}
