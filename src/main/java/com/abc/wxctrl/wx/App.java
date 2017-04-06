package com.abc.wxctrl.wx;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.util.NetUtils;
import org.h2.util.DateTimeUtils;
import org.w3c.dom.Document;

import com.abc.utility.io.IOUtil;
import com.abc.utility.io.NetUtil;
import com.abc.utility.misc.DateUtil;
import com.abc.utility.misc.JsonUtil;
import com.abc.utility.misc.RegexUtil;
import com.abc.utility.misc.XmlUtil;
import com.alibaba.fastjson.JSON;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
public class App {

	/**
	 * 解析微信响应内容
	 */
	public static Map<String, Object> getResponseByWxMap(String s) {
		Map<String, Object> itemMap = new HashMap<String, Object>();
		for (String itemStr : StringUtils.split(s, ";")) {
			String key = StringUtils.substringBefore(itemStr, "=").trim();
			String value = StringUtils.substringAfter(itemStr, "=").trim();
			if (value.startsWith("\"")) {
				itemMap.put(key, value.replaceAll("^\"|\"$", ""));
			} else if (value.startsWith("{")) {
				itemMap.put(key, JSON.parseObject(value));
			} else {
				itemMap.put(key, Integer.valueOf(value));
			}
		}
		return itemMap;
	}
}






