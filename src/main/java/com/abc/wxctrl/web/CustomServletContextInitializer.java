package com.abc.wxctrl.web;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;

import com.abc.wxctrl.config.AppConfigBean;

public class CustomServletContextInitializer implements ServletContextInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.setAttribute("app", AppConfigBean.INSTANCE);
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.asList(1102, 1101).contains(1101));
	}
}
