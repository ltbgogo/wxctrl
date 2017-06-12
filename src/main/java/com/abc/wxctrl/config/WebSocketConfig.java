package com.abc.wxctrl.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.abc.wxctrl.websocket.WxWebSocket;

@ConditionalOnWebApplication
@Configuration
public class WebSocketConfig {

	/**
	 * 注入ServerEndpointExporter，这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint。
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
	
	@Bean
	WxWebSocket wxWebSocket() {
		return new WxWebSocket();
	}
}
