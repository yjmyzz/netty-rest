package demo.soa.misc.common.util;

import java.security.MessageDigest;


public class MD5Util {

	// Use to translate the byte into hex char 
	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Encript string with MD5
	 * @param message: String to be Encripted
	 * @return : String that have been Encripted
	 */
	public static String getMD5(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = md.digest(message.getBytes("UTF-8"));
			return byteToHexString(b);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * convert byte[] into hex string
	 * 
	 * @param tmp     byte[] to be converted
	 * @return  hex string that have been converted
	 */
	private static String byteToHexString(byte[] tmp) {
		String s;

		char str[] = new char[16 * 2];

		int k = 0;
		for (int i = 0; i < 16; i++) {

			byte byte0 = tmp[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];

			str[k++] = hexDigits[byte0 & 0xf];
		}
		s = new String(str);
		return s;
	}
}
