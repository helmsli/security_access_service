package com.company.security.service;




import java.util.List;

import com.company.security.domain.SecurityRole;
import com.company.security.domain.SecurityRoleaction;

public interface SecurityRoleService {
	/**
	 * 更新权限列表，如果存在更新，不存在删除
	 * @param securityRole
	 */
	public int updateSecurityRole(List<SecurityRoleaction> securityRoleactions);
	
	/** 删除该角色所有的权限，用当前权限覆盖
	 * 更新权限列表，如果存在更新，不存在删除
	 * @param securityRole
	 */
	public int refreshSecurityRole(int roleId, List<SecurityRoleaction> securityRoleactions);
	
	/**
	 * 
	 * @param roleid
	 * @return
	 */
	public SecurityRole selectSecurityRole(long roleid);
	
	/**
	 * 删除该角色所有的权限
	 * @param roleid
	 * @return
	 */
	public int deleteRoleAllAction(long roleid);

	/**
	 * 
	 * @param securityRole
	 */
	public void insertRole(SecurityRole securityRole);
	
	/**
	 * 
	 * @param securityRole
	 * @return
	 */
	public int updateRole(SecurityRole securityRole);
	
	/**
	 * 
	 * @param securityRole
	 * @return
	 */
	public int deleteRole(int roleid);
	
}
