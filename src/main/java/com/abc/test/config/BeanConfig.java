package com.abc.test.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

public class BeanConfig {
	
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
	AppConfigBean appConfig() {
		return AppConfigBean.INSTANCE;
	}

	/**
	 * WebMvcConfigurerAdapter
	 */
	@Bean
	WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
		return new WebMvcConfigurerAdapterBean();
	}
}
