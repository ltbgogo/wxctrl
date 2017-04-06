package com.abc.wxctrl.wx;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.cookie.BasicClientCookie2;

import lombok.Cleanup;
import lombok.SneakyThrows;

import com.abc.utility.misc.JsonUtil;

public class Test {
	
//	private Map<String, String> requestHeaders = new LinkedHashMap<String, String>()

	@SneakyThrows
	public static void main(String[] args) {
//		System.setProperty("jsse.enableSNIExtension", "false");
//		https://wx.qq.com/");
		
		WxCookieStore cookieStore = new WxCookieStore();
		
		Map<String, String> requestHeaders = new LinkedHashMap<String, String>();
//		requestHeaders.put("Connection" , "keep-alive");
		
		@Cleanup
		WxHttpClient client = new WxHttpClient();
		client.setUrl("http://127.0.0.1:8090/tour/member/sso/preLogin.do");
		client.setMethod("POST");
		client.getRequestHeaderMap().putAll(requestHeaders);
		client.setCookieStore(cookieStore);
		client.connect();

		client = new WxHttpClient();
		client.setUrl("http://127.0.0.1:8090/tour/member/msg/addCookie.do");
		client.setMethod("POST");
		client.getRequestHeaderMap().putAll(requestHeaders);
		client.setCookieStore(cookieStore);		
		client.connect();
		System.out.println(cookieStore);
		client = new WxHttpClient();
		client.setUrl("http://127.0.0.1:8090/tour/member/msg/deleteCookie.do");
		client.setMethod("POST");
		client.getRequestHeaderMap().putAll(requestHeaders);
		client.setCookieStore(cookieStore);		
		client.connect();
		
		System.out.println(JsonUtil.toPrettyJson(client.getConnection().getHeaderFields()));
		
		System.out.println("**************8");

		System.out.println(cookieStore);
	}
	
	public static String getFirstHeader(HttpURLConnection conn) {
//		return conn.get
		return null;
	}
}
