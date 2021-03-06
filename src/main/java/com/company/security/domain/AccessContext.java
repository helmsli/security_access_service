package com.company.security.domain;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;

/**
 * 用户接入的各种上下文信息
 * @author helmsli
 *
 */
public class AccessContext implements Serializable{
	
	private PrivateKey rsaPrivateKey;
	
	private Object object;
	
	private String transid;
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

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}
	
	

	
	public PrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(PrivateKey rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}

	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "AccessContext [transid=" + transid + ", loginUserInfo=" + loginUserInfo + ", loginUserSession="
				+ loginUserSession + ", oldUserSession=" + oldUserSession + "]";
	}

	


	
	
	
	
	
}
