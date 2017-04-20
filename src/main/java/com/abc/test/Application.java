package com.abc.test;

import java.util.List;

import javax.activation.DataSource;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import lombok.SneakyThrows;

import org.hibernate.annotations.OptimisticLocking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.abc.test.config.AppConfig;
import com.abc.test.utility.SpringManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@SpringBootApplication
public class Application {
	
	@Bean
	ServletContextInitializer createServletContextInitializer() {
		return new ServletContextInitializer() {
			public void onStartup(ServletContext servletContext) throws ServletException {
				AppConfig.INSTANCE.setContextPath(servletContext.getContextPath());
				servletContext.setAttribute("app", AppConfig.INSTANCE);
			}
		};
	}
	
	@Bean
	AppConfig createAppConfig() {
		return AppConfig.INSTANCE;
	}
	
	@Bean
	SpringManager createSpringManager() {
		return new SpringManager();
	}
	
	@Bean
	WebMvcConfigurerAdapter createWebMvcConfigurerAdapter() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				 MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			        ObjectMapper objectMapper = new ObjectMapper();
			        /**
			         * 序列换成json时,将所有的long变成string，因为js中得数字类型不能包含所有的java long值
			         */
			        SimpleModule simpleModule = new SimpleModule();
			        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
			        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
			        objectMapper.registerModule(simpleModule);
			        converter.setObjectMapper(objectMapper);
			        converters.add(converter);
			}
	    };
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
