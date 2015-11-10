package demo.soa.misc.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具
 */
public class DateUtil
{
    private static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    
    public static final String FORMAT_MONTH = "yyyy-MM";
    public static final String FORMAT_MINUTES = "yyyy-MM-dd HH:mm";

    
    public static boolean isDate(String date){
    	return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    public static String getCurrentDateStr(){
    	return getCurrentDateStr(FORMAT_DATETIME);
    }
    
    public static String getDatetimeStr(long datetime){
    	return format(new Date(datetime),FORMAT_DATETIME);
    }
    
    public static String getDateStr(long datetime){
    	return format(new Date(datetime),FORMAT_DATE);
    }
	
    public static Date getCurrentDate(){
	     Calendar cal = Calendar.getInstance();
	     Date currDate = cal.getTime();
	     return currDate;
    }

    /**
	 * param   2010-09-01
	 */
    public static Date parseStringToDate(String date_str){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		Date date = null;
		try {
			if(date_str != null && date_str.length() > 0){
				date = sdf.parse(date_str);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
    
    public static Date parseStringToDate(String date_str,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			if(date_str != null && date_str.length() > 0){
				date = sdf.parse(date_str);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}    
    
    public static Integer parseStringToInt(String date_str){
    	if(date_str == null){
    		return null;
    	}
    	Date date = (date_str.length() > "2010-02-11 ".length()) ? parseStringToDateTime(date_str) 
    			: parseStringToDate(date_str);
		return date == null ? null : new Long(date.getTime()/1000).intValue();
	}
    
    /**
     * param   2010-09-01 19:29:10
     */
    public static Date parseStringToDateTime(String date_str){
    	SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME);
    	Date date = null;
    	try {
    		if(date_str != null && date_str.length() > 0){
    			date = sdf.parse(date_str);
    		}
    	} catch (ParseException e) {
    		e.printStackTrace();
    	}
    	return date;
    }
	
    private static String getCurrentDateStr(String strFormat){
	     Calendar cal = Calendar.getInstance();
	     Date currDate = cal.getTime();
	     return format(currDate, strFormat);
	 }

    public static String format(Date aTs_Datetime, String as_Pattern){
      if (aTs_Datetime == null || as_Pattern == null){
    	  return null;
      }
      SimpleDateFormat dateFromat = new SimpleDateFormat(as_Pattern);
      return dateFromat.format(aTs_Datetime);
    }
	public static void main(String[] args){
		System.out.println(getCurrentDateStr());
		System.out.println(parseStringToDateTime(getCurrentDateStr()).getTime());
		System.out.println(getDatetimeStr(System.currentTimeMillis()));
        System.out.println(getCurrentDateStr("yyyy.MM.dd HH:mm:ss"));
        System.out.println(format(new Date(System.currentTimeMillis()),"yyyy.MM.dd HH:mm:ss"));
	 }
	
	public static String timeStampToStr(Timestamp tm){
		String result = "";
		if(tm != null){
			result = DateUtil.getDatetimeStr(tm.getTime());
		}
		return result;
	}
	
	public static Integer currentTime(){
		return new Long(System.currentTimeMillis()/1000).intValue();
	}
	
	public static Integer currentTime(Date d){
		return new Long(d.getTime()/1000).intValue();
	}
	
	public static String fromUnixTimeStamp(long timestamp){
		String time = null;
		try{
			time = DateUtil.getDatetimeStr(timestamp*1000);
		}catch(NumberFormatException e){
		}
		return time;
	}
	
	public static Date unixTime2Date(long timestamp){
		return timestamp > 0 ? new Date(timestamp*1000) : null;
	}
	
	public static Date unixTime2Date(Integer timestamp){
		return timestamp !=null ? new Date(timestamp*1000) : null;
	}
	
	public static Date addForNow(int field, int amount){
		Calendar cal = Calendar.getInstance();
		cal.add(field, amount);
		return cal.getTime();
	}
	
	public static int daysBetween(Date originalDate, Date newDdate) throws ParseException    
	{    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		originalDate=sdf.parse(sdf.format(originalDate));  
		newDdate=sdf.parse(sdf.format(newDdate));  
		Calendar cal = Calendar.getInstance();    
		cal.setTime(originalDate);    
		long originalTime = cal.getTimeInMillis();                 
		cal.setTime(newDdate);    
		long newTime = cal.getTimeInMillis();         
		long between_days=(newTime-originalTime)/(1000*3600*24);  

		return Integer.parseInt(String.valueOf(between_days));           
	}

} 


