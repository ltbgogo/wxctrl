package com.abc.test.utility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.abc.test.utility.spec.InOutProcessor;

public class StringUtil {
	
	public static String escapeHtml(String html) {
		return html.replace("&lt;", "<").replace("&gt;", ">");
	}
	
	/**
	 * 编码引号之间的内容
	 * 这个方法用于处理或解析文档功能
	 */
	public static String encodeQuoted(String string, boolean isSingleQuote) {
		String quote = isSingleQuote ? "'" : "\"";
		return encodeQuoted(string, quote, "[^\\\\]" + quote, quote);
	}
	
	/**
	 * encodeQuoted("n1=\"v; \\\" c\"; Version=xxx\"\"sdf", "\"", "[^\\\\]\"", "\"");
	 */
	public static String encodeQuoted(String string, String startRegex, String stopRegex, final String quote) {
		String regex = String.format("%s.+?%s", startRegex, stopRegex);
		return RegexUtil.replace(string, regex, new InOutProcessor<String, String>() {
			public String process(String arg) throws Exception {
				arg = arg.substring(quote.length(), arg.length() - quote.length());
				return quote + Base64.encodeBase64String(arg.getBytes("UTF-8")) + quote; 
			}
		});
	}
	
	/**
	 * decodeQuoted("n1=\"djsgXCIgYw==\"; Version=xxx\"\"sdf", "\"");
	 */
	public static String decodeQuoted(String string, final String quote) {
		String regex = String.format("%1$s.+?%1$s", quote);
		return RegexUtil.replace(string, regex, new InOutProcessor<String, String>() {
			public String process(String arg) throws Exception { 
				arg = arg.substring(quote.length(), arg.length() - quote.length());
				return quote + new String(Base64.decodeBase64(arg), "UTF-8") + quote; 
			}
		});
	}

    public static boolean isAnyBlank(String...strings) {
    	for (String string : strings) {
    		if (StringUtils.isBlank(string)) return true;
    	}
    	return false;
    }
    
    public static boolean isAnyEmpty(String...strings) {
    	for (String string : strings) {
    		if (StringUtils.isEmpty(string)) return true;
    	}
    	return false;
    }
    
}
