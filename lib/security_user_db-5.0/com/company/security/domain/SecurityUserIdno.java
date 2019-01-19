package com.company.security.domain;

import java.io.Serializable;

/**
 * Model class of security_user_idNo.
 * 
 * @author generated by ERMaster
 * @version $Id$
 */
public class SecurityUserIdno implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 护照总号码. */
	private String idtotalno;

	/** 用户ID. */
	private long userId;

	/**
	 * Constructor.
	 */
	public SecurityUserIdno() {
	}

	/**
	 * Set the 护照总号码.
	 * 
	 * @param idtotalno
	 *            护照总号码
	 */
	public void setIdtotalno(String idtotalno) {
		this.idtotalno = idtotalno;
	}

	
	public static String getIdtotalno(int idtype,String idNo)
	{
		return (idtype + "*#" + idNo);
	}
	
	public void setIdtotalno(int idtype,String idNo) {
		setIdtotalno(idtype + "*#" + idNo);
	}
	
	public int getIdType() {
		String[] strS = idtotalno.split("*#");
		if(strS.length==2)
		{
			return Integer.parseInt(strS[0]);
		}
		return -1;
	}
	public String getIdNo() {
		String[] strS = idtotalno.split("*#");
		if(strS.length==2)
		{
			return (strS[1]);
		}
		return "";
	}
	

	/**
	 * Get the 护照总号码.
	 * 
	 * @return 护照总号码
	 */
	public String getIdtotalno() {
		return this.idtotalno;
	}

	/**
	 * Set the 用户ID.
	 * 
	 * @param userId
	 *            用户ID
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * Get the 用户ID.
	 * 
	 * @return 用户ID
	 */
	public long getUserId() {
		return this.userId;
	}



}