package com.abc.test.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class BeanConfig {

	/**
	 * 注入ServerEndpointExporter，这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint。
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
	
	/**
	 * ServletContextInitializer
	 */
	@Bean
	ServletContextInitializer servletContextInitializer() {
		return new ServletContextInitializeBean();
	}
	
	/**
	 * AppConfig
	 */
	@Bean
	AppConfig appConfig() {
		return AppConfig.INSTANCE;
	}

	/**
	 * WebMvcConfigurerAdapter
	 */
	@Bean
	WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
		return new WebMvcConfigurerAdapterBean();
	}
}
