package com.abc.wxctrl.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.abc.wxctrl.web.CustomApplicationReadyListener;
import com.abc.wxctrl.web.CustomServletContextInitializer;
import com.abc.wxctrl.web.CustomWebMvcConfigurerAdapter;

@ConditionalOnWebApplication
@Configuration
public class WebBeanConfig {
	
	/**
	 * ServletContextInitializer
	 */
	@Bean
	ServletContextInitializer servletContextInitializer() {
		return new CustomServletContextInitializer();
	}
	
	/**
	 * WebMvcConfigurerAdapter
	 */
	@Bean
	WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
		return new CustomWebMvcConfigurerAdapter();
	}
	
	/**
	 * ApplicationListener
	 */
	@Bean
	ApplicationListener<ApplicationReadyEvent> ApplicationReadyListener() {
		return new CustomApplicationReadyListener();
	}
}



