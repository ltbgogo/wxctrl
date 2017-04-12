package com.abc.test.utility.httpclient;

import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpClientConfig {

	private String method = "POST";
	private CookieStore cookieStore;
	private Map<String, Object> requestHeaderMap = new LinkedHashMap<String, Object>();
	private Proxy proxy;
	private Boolean enableSNIExtension;
}







