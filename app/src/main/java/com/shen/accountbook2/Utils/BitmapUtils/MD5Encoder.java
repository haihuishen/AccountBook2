package com.shen.accountbook2.Utils.BitmapUtils;

import java.security.MessageDigest;

/**
 * md5
 * 
 *
 */
public class MD5Encoder {
	
	/**
	 * MD5的加密(静态的!)
	 * 
	 * @param string		要加密的字符串
	 * @return				加密后的字符串
	 * 
	 * @throws Exception
	 */
	public static String encode(String string) throws Exception {
	    byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) {
	        	hex.append("0");
	        }
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
}
