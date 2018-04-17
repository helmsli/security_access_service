package com.company.security.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service("redisLockService")
public class RedisLockServiceImpl {
	@Resource(name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;
	/**
	 * 
	 * @param key
	 * @param accesskey
	 * @return
	 */
	public boolean isLockByOwner(String key, String accesskey) {
		try {
			ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
			String lockKey = key;
			String transValue = accesskey;
			// 获取redis的cache；
			String cacheTransValue = (String) opsForValue.get(lockKey);
			if (transValue.equalsIgnoreCase(cacheTransValue)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param key
	 * @param accessTime
	 */
	public synchronized void releaseUserTransLock(String key, String accesskey) {

		try {

			boolean lockOk = isLockByOwner(key, accesskey);
			if (lockOk) {
				// need to redo;maybe 3 times
				String lockKey = key;
				redisTemplate.delete(lockKey);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param key  -- 锁的key
	 * @param accessKey--加锁的业务key
	 * @param timeout--等待超时时间，毫秒
	 * @param tryInterval --多久重试获取锁，毫秒
	 * @param lockExpireTime--锁超时时间--单位秒
	 * @return
	 */
	public boolean  getUserTransLock(String key,String accessKey,long timeout,long tryInterval,long lockExpireTime){  
		long registerHProcess = -1;
        try{  
            
        	String lockKey = key;
            long startTime = System.currentTimeMillis();  
            String transValue = accessKey;
            while (true){
            	if(redisTemplate.opsForValue().setIfAbsent(lockKey,transValue)){  
                	redisTemplate.opsForValue().set(lockKey,transValue,lockExpireTime,TimeUnit.MILLISECONDS);  
                	
                	return true;  
                }
        
                //如果没有获取到，并且已经超时
                if(System.currentTimeMillis() - startTime > timeout){  
                    return false;  
                }  
                //延迟一段时间
                Thread.sleep(tryInterval);  
            }  
        }catch (Exception e){  
              e.printStackTrace();
            return false;  
        }  
        finally
        {
        	
        }
    }  

	
		
	public String  getCommonTransLock(String key,long timeout,long lockExpireSecond){  
		long registerHProcess = -1;
        try{  
            
        	String lockKey = key;
            long startTime = System.currentTimeMillis();  
            while (true){
            	String transValue = String.valueOf(System.currentTimeMillis());
                
            	if(redisTemplate.opsForValue().setIfAbsent(lockKey,transValue)){  
                	redisTemplate.opsForValue().set(lockKey,transValue,lockExpireSecond,TimeUnit.SECONDS);  
                	return transValue;  
                }
            	else
            	{
            		String reqTime = (String)redisTemplate.opsForValue().get(lockKey);
            		if(System.currentTimeMillis()-Long.parseLong(reqTime)>=lockExpireSecond*1000)
            		{
            			redisTemplate.opsForValue().set(lockKey,transValue,lockExpireSecond,TimeUnit.SECONDS);  
                    	return transValue;
            		}
            	}
        
                //如果没有获取到，并且已经超时
                if(System.currentTimeMillis() - startTime > timeout){  
                    return "";  
                }  
                //延迟一段时间
                Thread.sleep(1000);  
            }  
        }catch (Exception e){  
              e.printStackTrace();
            return null;  
        }  
        finally
        {
        	
        }
    }  
	
}
