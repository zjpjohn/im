package cn.xyz.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public final class DateUtil {

	public static final SimpleDateFormat FORMAT_YYYY_MM;
	public static final SimpleDateFormat FORMAT_YYYY_MM_DD;
	public static final SimpleDateFormat FORMAT_YYYY_MM_DD_HH_MM_SS;
	public static final Pattern PATTERN_YYYY_MM;
	public static final Pattern PATTERN_YYYY_MM_DD;
	public static final Pattern PATTERN_YYYY_MM_DD_HH_MM_SS;

	static {
		FORMAT_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
		FORMAT_YYYY_MM = new SimpleDateFormat("yyyy-MM");
		PATTERN_YYYY_MM_DD_HH_MM_SS = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}");
		PATTERN_YYYY_MM_DD = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
		PATTERN_YYYY_MM = Pattern.compile("[0-9]{4}-[0-9]{1,2}");
	}

	public static long currentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	public static String getFullString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getYMDString() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	public static String getYMString() {
		return new SimpleDateFormat("yyyyMM").format(new Date());
	}

	public static Date toDate(String strDate) {
		strDate = strDate.replaceAll("/", "-");
		try {
			if (PATTERN_YYYY_MM_DD_HH_MM_SS.matcher(strDate).find())
				return FORMAT_YYYY_MM_DD_HH_MM_SS.parse(strDate);
			else if (PATTERN_YYYY_MM_DD.matcher(strDate).find())
				return FORMAT_YYYY_MM_DD.parse(strDate);
			else if (PATTERN_YYYY_MM.matcher(strDate).find())
				return FORMAT_YYYY_MM.parse(strDate);
			else
				throw new RuntimeException("未知的日期格式化字符串");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long toTimestamp(String strDate) {
		return toDate(strDate).getTime();
	}

	public static long toSeconds(String strDate) {
		return toTimestamp(strDate) / 1000;
	}

	public static long s2s(String s) {
		s = StringUtil.trim(s);
		if ("至今".equals(s)) {
			return 0;
		}
		return toSeconds(s);
	}

	// public static String getDiff(long start, long end) {
	// long diff = end - start;
	// long day = diff / 86400;
	// long hour = diff % 86400 / 3600;
	// long minute = (diff % 86400 % 3600) / 60;
	// return MessageFormat.format("{0}天{1}时{2}分", diff / 86400, diff % 86400 /
	// 3600, (diff % 86400 % 3600) / 60);
	// }
	//
	// public static void main(String... args) {
	// long a = System.currentTimeMillis() + 86400 * 7;
	// System.out.println(a + 121);
	// System.out.println(getDiff(System.currentTimeMillis(), a + 79504));
	// System.out.println(getDiff(System.currentTimeMillis(), a + 121));
	// System.out.println(getDiff(System.currentTimeMillis(),
	// System.currentTimeMillis() + 121));
	//
	// long b = 1420788389937L;
	// System.out.println(b % 86400);
	// System.out.println(b % 86400 % 3600);
	// System.out.println((b % 86400) % 3600);
	// }
}
