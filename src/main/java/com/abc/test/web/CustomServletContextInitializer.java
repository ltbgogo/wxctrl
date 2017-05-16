package com.abc.test.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;

import com.abc.test.config.AppConfigBean;

public class CustomServletContextInitializer implements ServletContextInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {
		AppConfigBean.INSTANCE.setContextPath(servletContext.getContextPath());
		servletContext.setAttribute("app", AppConfigBean.INSTANCE);
	}
}
