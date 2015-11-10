package demo.soa.misc.common.util;

import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtil {

	
	public static String md5(String input) {
		if(input != null){
			return DigestUtils.md5Hex(input);
		}
		return null;
	}
	
}
