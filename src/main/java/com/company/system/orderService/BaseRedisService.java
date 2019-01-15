package com.company.system.orderService;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
@Service("baseRedisService")
public class BaseRedisService {

	@Resource(name = "redisTemplate")
	protected RedisTemplate<Object, Object> redisTemplate;

	@Resource(name = "redisTemplate")
	protected RedisTemplate<String, String> redisStringTemplate;

	@Autowired
	protected StringRedisTemplate stringRedisTemplate;

	/**
	 * 获取分布式锁
	 * @param lockKey  -- 分布式锁的key
	 * @param lockSeconds -- 锁定秒数
	 * @param waitSeconds -- 等待锁的秒数，等待超过该时间，返回超时
	 * @return  0--超时，申请到锁的事务号，需要按照该事务号释放锁
	 */
	public long getCommonLock(String lockKey, int lockSeconds, int waitSeconds) {
		try {

			long startTime = System.currentTimeMillis();
			boolean needWait = false;
			while (true) {
				{
					if (redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(startTime))) {
						startTime = System.currentTimeMillis();
						redisTemplate.opsForValue().set(lockKey, String.valueOf(startTime), lockSeconds, TimeUnit.SECONDS);
						return startTime;
					}
					//如果是第一次进来，最好判断一下是否老的已经过期，否则会死锁
					else if (!needWait) {
						needWait = true;
						String requestTimeS = (String) redisTemplate.opsForValue().get(lockKey);
						//获取时间
						if (requestTimeS != null) {
							long requestTimeL = 0;
							try {
								requestTimeL = Long.parseLong(requestTimeS);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//如果没有获取到，并且已经超时
							if (System.currentTimeMillis() - requestTimeL > lockSeconds * 1000) {
								redisTemplate.delete(lockKey);
								continue;
							}
						}
						//如果没有时间
						else {
							redisTemplate.delete(lockKey);
							continue;
						}
					}
				}
				//如果没有获取到，并且已经超时
				if (System.currentTimeMillis() - startTime > waitSeconds * 1000) {
					return 0;
				}
				//延迟一段时间
				Thread.sleep(300);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * 释放锁
	 * @param lockKey -- 锁的key
	 * @param requestTime  -- 锁的事务号
	 */
	public void releaseCommonLock(String lockKey, long requestTime) {
		try {
			String requestTimeS = (String) redisTemplate.opsForValue().get(lockKey);
			long requestTimeL = Long.parseLong(requestTimeS);
			if (requestTimeL == requestTime) {
				redisTemplate.delete(lockKey);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RedisTemplate<Object, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public RedisTemplate<String, String> getRedisStringTemplate() {
		return redisStringTemplate;
	}

	public StringRedisTemplate getStringRedisTemplate() {
		return stringRedisTemplate;
	}
	
	

}
