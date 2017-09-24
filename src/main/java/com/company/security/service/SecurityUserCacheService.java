package com.company.security.service;

import java.util.List;

import com.company.security.domain.LoginUser;
import com.company.security.domain.LoginUserSession;
import com.company.security.domain.SecurityUser;

public interface SecurityUserCacheService {
	/**
	 * 
	 * @param loginUser
	 * @return
	 */
	public boolean putBasicInfo(LoginUser loginUser);
	
	/**
	 * 
	 * @param token
	 * @return 0 -- 如果没有token数据返回0
	 */
	public long getSessionAccessTime(String token);
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public boolean delSessionAccessTime(String token);
	/**
	 * 设置token过期时间
	 * @param token
	 * @param accessTime
	 * @param duartionSeconds --内存中保留token的描述
	 * @return
	 */
	public boolean setSessionAccessTime(String token,long accessTime,int duartionSeconds);
	
	/**
	 * 设置session信息，负责将老的session信息清除
	 * @param loginUserSession
	 * @param oldUserSession
	 * @return
	 */
	public boolean putSessionInfo(LoginUserSession loginUserSession,LoginUser loginUser,int duartionSeconds);
	
	/**
	 * 根据登录类型和用户ID或者token信息
	 * @param loginType -- pad/mobile/web
	 * @param userId   
	 * @return
	 */
	public LoginUserSession getSessionInfo(int loginType,long userId);
	
	/**
	 * 获取基本信息
	 * @param userId
	 * @return
	 */
	public LoginUser getBasicInfo(long userId);
	
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public LoginUser getBInfoByPhone(String phone);
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public LoginUser getBInfoByEmail(String email);
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public boolean removeBasinInfo(long userId);
	
	
	
	
	
	/**
	 * 获取用户基本信息最后更新时间
	 * @param securityUser
	 */
	public long getLastModifyTime(long userId);
	
	/**
	 * 
	 * @param phone
	 * @return
	 */
	public long getLastModifyTime(String phone);
	
	
	
	
	/**
	 * 设置用户基本信息最后更新时间
	 * @param userId
	 * @param lastModifyTime
	 * @return
	 */
	public boolean putLastModifyTime(LoginUser loginUser,long lastModifyTime);
		
	/**
	 * 申请新创建的userid
	 * @param numbers --申请的userid的个数
	 * @return -- 增加后的数值
	 */
	public long createUserId(int numbers);
	
	
	/**
	 * 
	 * @param loginId
	 * @return  使用：分割，返回事务号和随机码
	 */
	public String getTrandsId(String loginId);
	/**
	 * 验证随机码和事务号
	 * @param loginId
	 * @return
	 */
	public String getRandomByTransid(String loginId,String transid);
	
		
}
