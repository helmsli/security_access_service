package com.company.security.domain;

/**
 * 用户接入的各种上下文信息
 * @author helmsli
 *
 */
public class AccessContext {
	/**
	 * 登录成功的用户静态信息
	 */
    private LoginUser loginUserInfo;
    
   
    
    /**
     * 用户登录成功后的动态信息
     */
    private LoginUserSession loginUserSession ; 
    
	
    private LoginUserSession oldUserSession ; 
    
    
	/**
	 * 
	 * @param loginUserInfo
	 */
	public void setLoginUserInfo(LoginUser loginUserInfo) {
		this.loginUserInfo = loginUserInfo;
	}
	
	public LoginUser getLoginUserInfo() {
		return loginUserInfo;
	}

	public LoginUserSession getLoginUserSession() {
		return loginUserSession;
	}


	public void setLoginUserSession(LoginUserSession loginUserSession) {
		this.loginUserSession = loginUserSession;
	}

	public LoginUserSession getOldUserSession() {
		return oldUserSession;
	}

	public void setOldUserSession(LoginUserSession oldUserSession) {
		this.oldUserSession = oldUserSession;
	}


	
	
	
	
	
}
