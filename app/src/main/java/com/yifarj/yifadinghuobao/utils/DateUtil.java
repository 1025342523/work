package com.yifarj.yifadinghuobao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtil {
	private static SimpleDateFormat format8 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
	private static SimpleDateFormat format9 = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);

	public static String getFormatDate(long timeMillis) {
		return format8.format(new Date(timeMillis));
	}

	public static String getFormatTime(long timeMillis) {
		return format9.format(new Date(timeMillis));
	}
}
