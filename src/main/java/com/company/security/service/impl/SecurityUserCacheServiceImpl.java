package com.company.security.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.service.SecurityUserCacheService;
@Service("securityUserCacheService")
public class SecurityUserCacheServiceImpl extends SecurityUserCacheKeyService implements SecurityUserCacheService {
	
	@Resource (name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	
	@Resource(name="redisLockService")
	private RedisLockServiceImpl redisLockService;
	
	/**
	 * token在内存中失效的天数,仅仅时内存中保留的天数，不是token失效的天数
	 */
	protected int tokenExpiredDays = 5;
	@Override
	public boolean putBasicInfo(LoginUser loginUser) {
		String lockKey = this.getLockkey(loginUser.getUserId());
		String transTime = String.valueOf(System.currentTimeMillis());
		boolean isLock = getLock(lockKey, transTime);
		
		try {
			// TODO Auto-generated method stub
			if(isLock)
			{
				this.cleanAllUserCache(loginUser.getUserId());
				ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
				String userKey = this.getLoginUserkey(loginUser.getUserId());
				opsForValue.set(userKey, loginUser);
				String emailkey = this.getEmailKey(loginUser.getEmail());
				opsForValue.set(emailkey, new Long(loginUser.getUserId()));
				String phoneKey = this.getPhoneKey(loginUser.getPhone());
				opsForValue.set(phoneKey, new Long(loginUser.getUserId()));
					
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
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		
		String userKey = this.getLoginUserkey(userId);
		LoginUser loginUser = (LoginUser)opsForValue.get(userKey);
		return loginUser;
		
	}

	@Override
	public LoginUser getBInfoByPhone(String phone) {
		// TODO Auto-generated method stub
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String phoneKey = this.getPhoneKey(phone);
		Long  userid = (Long)opsForValue.get(phoneKey);
		return getBasicInfo(userid.longValue());
	}

	@Override
	public LoginUser getBInfoByEmail(String email) {
		// TODO Auto-generated method stub
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String emailkey = this.getEmailKey(email);
		Long  userid = (Long)opsForValue.get(emailkey);
		return getBasicInfo(userid.longValue());
	}

	@Override
	public boolean removeBasinInfo(long userId) {
		// TODO Auto-generated method stub
		String key = this.getLoginUserkey(userId);
		redisTemplate.delete(key);
		return true;
	}
	@Override
	public long getLastModifyTime(long userId) {
		// TODO Auto-generated method stub
		String lockKey = this.getLockkey(userId);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String lastModifyTime = (String)opsForValue.get(lockKey);
		return Long.parseLong(lastModifyTime);
	}
	/**
	 * 清除所有缓存
	 * @param userId
	 */
	protected void cleanAllUserCache(long userId)
	{
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
	}
	@Override
	public boolean putLastModifyTime(long userId, long lastModifyTime) {
		// TODO Auto-generated method stub
		String lockKey = this.getLockkey(userId);
		String transTime = String.valueOf(System.currentTimeMillis());
		boolean isLock = getLock(lockKey,transTime);
		try {
			if (isLock) {
               String modifyKey = this.getLastModifyKey(userId);
               cleanAllUserCache(userId);
               ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
               opsForValue.set(modifyKey, String.valueOf(lastModifyTime));               
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
		return redisLockService.getUserTransLock(key, accesskey, 15000, 1000, 15000);				 
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
		String tokenkey = this.getTokenKey(loginUserSession.getLoginType(), loginUserSession.getUserId());
		opsForValue.set(tokenkey, loginUserSession,tokenExpiredDays,TimeUnit.DAYS);
		this.setSessionAccessTime(loginUserSession.getToken(), System.currentTimeMillis(), duartionSeconds);		
		return true;
	}
	@Override
	public boolean putSessionInfo(LoginUserSession loginUserSession,int duartionSeconds) {
		String lockKey = this.getLockkey(loginUserSession.getUserId());
		String transTime = String.valueOf(System.currentTimeMillis());
		
		boolean isLock = getLock(lockKey, transTime);
		
		try {
			// TODO Auto-generated method stub
			if(isLock)
			{
				
				refreshSessionInfo(loginUserSession,duartionSeconds);
			}
			return isLock;
		} 
		catch(Exception e)
		{
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
		return (LoginUserSession)(opsForValue.get(tokenkey));
	}

	@Override
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

	@Override
	public boolean setSessionAccessTime(String token, long accessTime,int duartionSeconds) {
		String accessKey  =this.getTokenAccessKey(token);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		opsForValue.set(accessKey, String.valueOf(accessTime),duartionSeconds,TimeUnit.SECONDS);
		return true;
	}

	@Override
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
		Long retValue= opsForValue.increment(key, numbers);
		return retValue.longValue() + 110000000;
	}	
	

}
