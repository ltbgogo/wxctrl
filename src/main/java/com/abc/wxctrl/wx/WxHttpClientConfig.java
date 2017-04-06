package com.abc.wxctrl.wx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
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
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.abc.utility.misc.JsonUtil;
import com.abc.utility.web.WebUtil;
import com.alibaba.fastjson.JSON;

@Getter
@Setter
public class WxHttpClientConfig {

	private String method = "POST";
	private WxCookieStore cookieStore;
	private Map<String, Object> requestHeaderMap = new LinkedHashMap<String, Object>();
	private Proxy proxy;
	private Boolean enableSNIExtension;
}







