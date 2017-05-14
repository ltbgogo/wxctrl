package com.abc.test.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;

public class ServletContextInitializeBean implements ServletContextInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {
		AppConfigBean.INSTANCE.setContextPath(servletContext.getContextPath());
		servletContext.setAttribute("app", AppConfigBean.INSTANCE);
	}
}
