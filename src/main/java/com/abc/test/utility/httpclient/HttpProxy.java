package com.abc.test.utility.httpclient;

import java.io.Serializable;
import java.net.Proxy;
import java.net.SocketAddress;

public class HttpProxy extends Proxy implements Serializable {

	public HttpProxy(Type type, SocketAddress sa) {
		super(type, sa);
	}

	
}
