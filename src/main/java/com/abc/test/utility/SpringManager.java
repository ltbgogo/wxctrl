package com.abc.test.utility;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.JpaTransactionManager;

public class SpringManager implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext arg0) {
		applicationContext = arg0;
	}
	
	public static EntityManagerFactory getEntityManagerFactory() {
		return getBean(JpaTransactionManager.class).getEntityManagerFactory();	
	}
	
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}
	
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}
	
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
}
