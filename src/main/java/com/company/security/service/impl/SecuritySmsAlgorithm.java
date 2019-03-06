package com.company.security.service.impl;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import org.springframework.util.DigestUtils;



public  class SecuritySmsAlgorithm {
	public static String getCrcString(String key,SmsInfo smsInfo)
	{
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuilder ls = new StringBuilder();
			ls.append(key);
			ls.append("*");
			ls.append(smsInfo.getTransId());
			ls.append("*");
			ls.append(smsInfo.getSmsTemplateCode());
			ls.append("*");
			ls.append(smsInfo.getCalledPhoneNumbers());
			ls.append("*");
			ls.append(smsInfo.getDestAppName());
			ls.append("*");
			ls.append(smsInfo.getParameters());
			ls.append("*");
			ls.append(formatter.format(smsInfo.getRequestTime()));
			return DigestUtils.md5DigestAsHex(ls.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 比较密码
	 * @param key
	 * @param source
	 * @param md5String
	 * @return
	 */
	public static boolean checkCrc(String key,SmsInfo smsInfo)  {
		try {
			String crcString = getCrcString(key,smsInfo);
			return smsInfo.getCheckCrc().equalsIgnoreCase(crcString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
