package com.abc.test;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;

import com.abc.test.config.AppConfig;
import com.abc.test.utility.SpringManager;

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
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
