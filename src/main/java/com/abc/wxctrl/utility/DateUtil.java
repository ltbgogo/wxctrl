package com.abc.wxctrl.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lombok.SneakyThrows;

public class DateUtil {
	
	public static final int SECONDS_PER_SECOND = 1;
	public static final int SECONDS_PER_MINUTE = 60 * SECONDS_PER_SECOND;
	public static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
	public static final int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
	public static final int SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;
	
	/**
	 * 返回偏移当前时间
	 */
	public static Date offset(long offsetInMillis) {
		return new Date(new Date().getTime() + offsetInMillis);
	}
	
	/**
	 * 放回当前时间，单位是秒
	 */
	public static int seconds() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	public static Date parseGMT(String str) {
		return parse(Locale.US, str, "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'");
	}

	/**
	 * 此方法仅限中国区域
	 */
	@SneakyThrows
	public static Date parse(Locale locale, String str, String...parsePatterns) {
        SimpleDateFormat parser = new SimpleDateFormat("", locale);
        //设置东八区
        parser.setTimeZone(TimeZone.getTimeZone("GMT")); 
        for (String parsePattern : parsePatterns) {
        	try {
        		parser.applyPattern(parsePattern);
                return parser.parse(str);
        	} catch (ParseException e) {
        	}
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }
}
