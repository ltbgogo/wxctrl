package com.abc.wxctrl.wx;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.abc.utility.misc.JsonUtil;
import com.abc.utility.web.WebUtil;
import com.alibaba.fastjson.JSON;

@Log4j
@NoArgsConstructor
public class WxHttpClient implements Closeable {
	
	public WxHttpClient(WxHttpClientConfig config) {
		this.method = config.getMethod();
		this.cookieStore = config.getCookieStore();
		this.requestHeaderMap = config.getRequestHeaderMap();
		this.enableSNIExtension = config.getEnableSNIExtension();
		this.proxy = config.getProxy();
	}
	
	public WxHttpClient(String url) {
		this.url = url;
	}
	
	public WxHttpClient(String url, WxHttpClientConfig config) {
		this(config);
		this.url = url;
	}
	
	@Setter
	private Boolean enableSNIExtension;
	@Setter
	private Proxy proxy; //new Proxy(Type.HTTP, new InetSocketAddress("proxy3.bj.petrochina", 8080));
	@Setter
	private String url;
	@Setter
	private String method = "POST";
	@Setter
	private WxCookieStore cookieStore;
	@Getter
	@Setter
	private Map<String, Object> requestHeaderMap = new LinkedHashMap<String, Object>();
	/**
	 * 此参数会拼接到URL后面
	 */
	@Getter
	@Setter
	private Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
	/**
	 * 放到请求体中的数据，用于POST请求
	 */
	@Getter
	@Setter
	private Map<String, Object> contentMap = new LinkedHashMap<String, Object>();
	@Setter
	private Object contentJson;
	@Setter
	@Getter
	private Map<String, Object> contentJsonOfMap = new LinkedHashMap<String, Object>();
	@Setter
	private Object contentString;
	@Setter
	private InputStream contentStream;
	@Getter
	private HttpURLConnection connection;
	
	@SneakyThrows
	public WxHttpClient connect() {
		String urlStr = this.url;
		if (!this.queryMap.isEmpty()) {
			String queryString = WebUtil.getQueryString(queryMap);
			if (urlStr.contains("?")) {
				if (urlStr.endsWith("&")) {
					urlStr += queryString;
				} else {
					urlStr += "&" + queryString;
				}
			} else {
				urlStr += "?" + queryString;
			}
		}
		URL url = new URL(urlStr);
		
		if (this.enableSNIExtension != null) {
			System.setProperty("jsse.enableSNIExtension", this.enableSNIExtension.toString());
		}
		//代理设置
		if (this.proxy != null) {
			this.connection = (HttpURLConnection) url.openConnection(proxy);
		} else {
			this.connection = (HttpURLConnection) url.openConnection();
		}
		//默认请求头
		this.connection.setRequestProperty("User-Agent" , "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");
		this.connection.setRequestProperty("Accept", "*/*");
		this.connection.setRequestProperty("Connection", "keep-alive");
		//请求cookie
		if (this.cookieStore != null && !this.cookieStore.isEmpty()) {
			this.connection.setRequestProperty("Cookie", this.cookieStore.toString());
		}
		//不自动重定向
		this.connection.setInstanceFollowRedirects(false);
		//请求方法
		this.connection.setRequestMethod(this.method.toUpperCase());
		//请求头
		for (String key : this.requestHeaderMap.keySet()) {
			this.connection.setRequestProperty(key, this.requestHeaderMap.get(key).toString());
		}
		//请求内容
		//关于常规格式的请求内容
		if (!this.contentMap.isEmpty()) {
			this.contentString = WebUtil.getQueryString(this.contentMap);
		}
		//关于json格式的请求内容
		if (!this.contentJsonOfMap.isEmpty()) {
			this.contentJson = this.contentJsonOfMap;
		}
		if (this.contentJson != null) {
			this.contentString = JsonUtil.toPrettyJson(this.contentJson);
		}
		//关于字符串格式请求内容
		if (this.contentString != null) {
			this.contentStream = IOUtils.toInputStream(contentString.toString(), "UTF-8");
		}
		//关于流格式请求内容
		if (this.contentStream != null) {
			this.connection.setDoOutput(true);
			@Cleanup
			OutputStream out = this.connection.getOutputStream();
			IOUtils.copy(contentStream, out);
		}
		//开始连接，注释掉此行，后面的代码也会隐式打开连接
		this.connection.connect();
		//响应cookie
		String setCookie = this.connection.getHeaderField("Set-Cookie");
		if (setCookie != null && this.cookieStore != null) {
			List<String> cookieStrs = this.connection.getHeaderFields().get("Set-Cookie");
			for (int i = cookieStrs.size(); i > 0; i--) {
				this.cookieStore.addCookie(new WxCookie(cookieStrs.get(i - 1)));
			}
		}
		return this;
	}
	
	@SneakyThrows
	public byte[] getResponseByBytes() {
		@Cleanup
		InputStream in = this.connection.getInputStream();
		byte[] data = IOUtils.toByteArray(in);
		FileUtils.writeByteArrayToFile(new File("e://test/wx/urls/" + this.url.replaceAll("[:/]", "_").split("\\?")[0] + "_" + System.nanoTime() + ".txt"), data);
		return data;
	}
	
	@SneakyThrows
	public String getResponseByString() {
		byte[] data = getResponseByBytes();
		return new String(data, "UTF-8");
	}
	
	@SneakyThrows
	public <T> T getResponseByJson(Class<T> requiredType) {
		return JSON.parseObject(getResponseByString(), requiredType);
	}
	
	public Map<String, List<String>> getResponseHeaders() {
		Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
		for (Entry<String, List<String>> header : this.connection.getHeaderFields().entrySet()) {
			List<String> values = new ArrayList<String>(header.getValue());
			Collections.reverse(values);
			headers.put(header.getKey(), values);
		}
		return headers;
	}
	
	/**
	 * 解析微信响应内容
	 */
	public Map<String, Object> getResponseByWxMap() {
		Map<String, Object> itemMap = new HashMap<String, Object>();
		for (String itemStr : StringUtils.split(getResponseByString(), ";")) {
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
	
	@SneakyThrows
	public int getResponseCode() {
		return this.connection.getResponseCode();
	}

	public void close() {
		if (this.connection != null) {
			this.connection.disconnect();	
		}
	}
}







