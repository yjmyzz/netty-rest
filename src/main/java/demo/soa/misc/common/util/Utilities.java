package demo.soa.misc.common.util;

import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 此类用来做工具类集合
 */
public class Utilities {
    private static final ThreadPoolExecutor executer = new ThreadPoolExecutor(3, 5, 10
            , TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(100000));

    public static ThreadPoolExecutor getExecutor() {
        return executer;
    }

    /**
     * 产生数字和字母混合的指定位数的随机密码
     *
     * @param count 位数
     * @return 随机密码
     */
    public static String getRandomPwd(int count) {
        String randomPwd = RandomStringUtils.random(count, true, false);
        return randomPwd;
    }

    public static String getRandomNumber(int count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static Integer parseInt(String num) {
        Integer n = null;
        try {
            n = (num == null) ? null : Integer.parseInt(num);
        } catch (Exception e) {

        }
        return n;
    }

    public static Byte parseByte(String num) {
        Byte n = null;
        try {
            n = (num == null) ? null : Byte.parseByte(num);
        } catch (Exception e) {

        }
        return n;
    }

    public static Short parseShort(String num) {
        Short n = null;
        try {
            n = (num == null) ? null : Short.parseShort(num);
        } catch (Exception e) {

        }
        return n;
    }

    public static Long parseLong(String num) {
        Long n = null;
        try {
            n = (num == null) ? null : Long.parseLong(num);
        } catch (Exception e) {

        }
        return n;
    }

    public static Boolean parseBoolean(String num) {
        Boolean n = null;
        try {
            n = (num == null) ? null : Boolean.parseBoolean(num);
        } catch (Exception e) {

        }
        return n;
    }

    public static StringBuilder appendParam(StringBuilder returnStr, String paramId, String paramValue) {
        if (returnStr.length() > 0) {
            if (paramValue != null && !paramValue.equals("")) {
                returnStr.append("&").append(paramId).append("=").append(paramValue);
            }
        } else {
            if (paramValue != null && !paramValue.equals("")) {
                returnStr.append(paramId).append("=").append(paramValue);
            }
        }
        return returnStr;
    }


}
