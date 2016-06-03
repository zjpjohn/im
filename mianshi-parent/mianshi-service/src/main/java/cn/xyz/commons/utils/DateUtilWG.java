package cn.xyz.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;


/**
 * 日期工具类
 * @author zhangjy
 *
 */
public class DateUtilWG {
    
	private static final SimpleDateFormat SIMPLEDATE = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat FULLDATE = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final SimpleDateFormat TIME = new SimpleDateFormat("HHmmss");
	private static final SimpleDateFormat GLNZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final SimpleDateFormat GLNZ1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
	
	public static Date getSimpleDate(String source){
		Date date = null;
		
		if(StringUtils.isBlank(source))return null;
		try {
			date = SIMPLEDATE.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static String getSimpleStringDate(Date date){
		String dateStr = null;
		try {
			dateStr = SIMPLEDATE.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateStr; 
	}
	
	public static String getSimpleStringTime(Date date){
		String dateStr = null;
		try {
			dateStr = TIME.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateStr; 
	}
	
	public static String getFullStringDate(Date date){
		String dateStr = null;
		try {
			dateStr = FULLDATE.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateStr; 
	}
	
	public static Date getFullDate(String source){
		Date date = null;
		try {
			date = FULLDATE.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
//	public static String getFillGLDate(String source){
//        
//        Date date = null;
//		try {
//			date = GLNZ1.parse(source);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        
//        GregorianCalendar ca = new GregorianCalendar(TimeZone.getTimeZone("GMT 00:00"));  
//        ca.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 
//                cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));  
//        
//        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
//        format.setTimeZone(TimeZone.getDefault());  
//        return format.format(ca.getTime());
//	}
	
	public static void main(String[] args) throws ParseException {
	        System.out.println(getSimpleStringDate(new Date()));
	        System.out.println(getFullStringDate(new Date()));
	        System.out.println(getSimpleStringTime(new Date()));
	}
}
