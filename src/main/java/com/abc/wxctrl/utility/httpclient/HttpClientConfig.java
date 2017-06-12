package com.abc.wxctrl.utility.httpclient;

import java.io.Serializable;
import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpClientConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String method = "POST";
	private CookieStore cookieStore;
	private Map<String, Object> requestHeaderMap = new LinkedHashMap<String, Object>();
	private String httpProxy;
	private Boolean enableSNIExtension;
}







