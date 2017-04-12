package com.abc.test.wx;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.commons.io.FileUtils;

import com.abc.test.utility.httpclient.CookieStore;
import com.abc.test.utility.httpclient.HttpClientConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WxMeta {
	
	public String status = "ok"; //ok failure 
	public String frontMsg;

	public String base_uri;
	public String redirect_uri;
	public String webpush_url;
	
	public String uuid;
	
	public String skey;
	public String synckeyStr;
	public String wxsid;
	public String wxuin;
	public String pass_ticket;
	public String deviceId = "e" + System.nanoTime();
		
	public JSONObject baseRequest;
	public JSONObject syncKey;
	public JSONObject user;
	
	public File dir_root = new File("d://test/wx");
	public File file_qrCode = FileUtils.getFile(dir_root, "qrcode." + deviceId + ".jpg");
	
	public HttpClientConfig httpClientConfig;
	
	// 微信联系人列表，可聊天的联系人列表
	public JSONArray memberList;
	public JSONArray contactList;
	public JSONArray groupList;
	
	public WxMeta() {
		httpClientConfig = new HttpClientConfig();
		httpClientConfig.getRequestHeaderMap().put("Host", "wx.qq.com");
		httpClientConfig.getRequestHeaderMap().put("Referer", "https://wx.qq.com/?&lang=zh_CN");
		httpClientConfig.setCookieStore(new CookieStore());
		httpClientConfig.setProxy(new Proxy(Type.HTTP, new InetSocketAddress("10.22.98.21", 8080)));
		httpClientConfig.setEnableSNIExtension(false);
	}
}






