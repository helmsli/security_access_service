package com.company.security.token;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;


@Service("tokenService")
public class TokenServiceImpl implements TokenService{

	@Resource (name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	public  final String Key_prefix_TokenExpired= "luExpir:";
	
	private int durationSeconds = 24*3600;
	
	 private Logger logger = LoggerFactory.getLogger(getClass());

	 
	@Value("${token.expireMillSeconds:1800000}")  
	private int tokenExpireSeconds;
	/**
	 * 获取用户安全级别的token
	 * @param token
	 * @return
	 */
	public  String getTokenAccessKey(String token)
	{
		StringBuilder str= new StringBuilder();
		str.append(Key_prefix_TokenExpired);
		str.append(token);
		return str.toString();
	}
	@Override
	public boolean setTokenInfo(String token, TokenInfo tokenInfo, int duartionSeconds) {
		// TODO Auto-generated method stub
		String accessKey  =getTokenAccessKey(token);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		//tokenInfo.setCreateTime(System.currentTimeMillis());
		opsForValue.set(accessKey, String.valueOf(System.currentTimeMillis()),duartionSeconds,TimeUnit.SECONDS);
		//this.durationSeconds = duartionSeconds;
		return true;

	}

	@Override
	public TokenInfo getTokenInfo(String token) {
		// TODO Auto-generated method stub
		String accessKey  =getTokenAccessKey(token);
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		String createTime = (String)opsForValue.get(accessKey);
		TokenInfo tokenInfo = null;
		
		if(createTime==null)
		{
			return null;
		}
		else
		{
			tokenInfo = new TokenInfo();
			long tokenCreateTime = Long.parseLong(createTime);
			tokenInfo.setCreateTime(tokenCreateTime);
			if(System.currentTimeMillis() - tokenCreateTime>tokenExpireSeconds)
			{
				return null;
			}
			if(System.currentTimeMillis() - tokenCreateTime>600000)
			{
				
				setTokenInfo(token,null,durationSeconds);
			}
		}
	
		return tokenInfo;
	}

	@Override
	public TokenInfo checkTokenInfo(String token) {
		// TODO Auto-generated method stub
		String accessKey  =getTokenAccessKey(token);
		TokenInfo tokenInfo=null;
		try {
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			String createTime = (String)opsForValue.get(accessKey);
			if(createTime==null)
			{
				logger.debug("token is null");
				return null;
			}
			else
			{
				tokenInfo = new TokenInfo();
				tokenInfo.setCreateTime(Long.parseLong(createTime));
				if(System.currentTimeMillis() - tokenInfo.getCreateTime()>tokenExpireSeconds)
				{
					logger.debug("token is expire:" + tokenInfo.getCreateTime());
					return null;
				}
				if(System.currentTimeMillis() - tokenInfo.getCreateTime()>600)
				{
					setTokenInfo(token,tokenInfo,durationSeconds);
				}
			}
			return tokenInfo;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean delTokenInfo(String token) {
		// TODO Auto-generated method stub
		logger.debug("delete old token:"+token);
		String accessKey  =getTokenAccessKey(token);
		redisTemplate.delete(accessKey);
		return true;
	}

}
