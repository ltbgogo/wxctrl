package com.abc.wxctrl.utility.httpclient;

import java.io.Serializable;
import java.net.Proxy;
import java.net.SocketAddress;

public class HttpProxy extends Proxy implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public HttpProxy(Type type, SocketAddress sa) {
		super(type, sa);
	}

	
}
