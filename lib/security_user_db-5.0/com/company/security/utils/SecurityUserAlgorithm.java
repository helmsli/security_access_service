package com.company.security.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.util.DigestUtils;

public  class SecurityUserAlgorithm {
	public static final String Prop_split="*";
	/**
	 * 采用Md5加密
	 * @param key
	 * @param source
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String EncoderByMd5(String key,String source) {
		try {
			StringBuilder ls = new StringBuilder();
			ls.append(key);
			ls.append("****");
			ls.append(source);
			
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
	public static boolean checkByMd5(String key,String source,String md5String)  {
		try {
			String encodeMd5 = EncoderByMd5(key,source);
			return encodeMd5.equalsIgnoreCase(md5String);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
