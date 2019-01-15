package com.company.security.service;

import java.util.List;



import com.company.security.domain.SecurityAllaction;

public interface SecurityActionService {
	/**
	 * 插入的菜单或者按钮信息，是系统的菜单或者按钮信息
	 * @param securityAllaction
	 */
	public void insertSecurityaction(SecurityAllaction securityAllaction);
	/**
	 * 
	 * @return
	 */
	public List<SecurityAllaction> selectAllAction();
	
	/**
	 * 
	 * @param actionId
	 * @return
	 */
	public SecurityAllaction selectActionByid(long actionId);
	
	/**
	 * 
	 * @param system
	 * @return
	 */
	public List<SecurityAllaction> selectActionBySystem(String system);	
	
	/**
	 * 
	 * @param securityAllaction
	 * @return
	 */
	public int updateAction(SecurityAllaction securityAllaction);	
	
	/**
	 * 
	 * @param actionId
	 * @return
	 */
	public int deleteActionByid(long actionId);

}
