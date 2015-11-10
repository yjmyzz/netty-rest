package demo.soa.misc.common.util;

import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;


public class StringUtil {

	public static boolean isNumber(String str){
		return StringUtils.isNumeric(str);
	}
	
	public static boolean isMobile(String mobile){
		String pMobile = "^(1(([34578][0-9])))\\d{8}$";
		return Pattern.matches(pMobile, mobile);
	}
	
	public static boolean isEmail(String email){
		if((email.indexOf("@") == -1)){
			return false;
		}
		String pEmail = "^[\\w-]{1,40}(\\.[\\w-]{1,20}){0,6}@[\\w-]{1,40}(\\.[\\w-]{1,20}){1,6}$";
		return Pattern.matches(pEmail, email);
	}
}
