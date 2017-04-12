package com.abc.test.repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.abc.test.utility.SpringManager;

/**
 * 数据仓库工程
 */
public interface RepoFactory {

	WxAccountRepository getWxAccountRepo();
	UserRepository getUserRepo();
	
	RepoFactory f = (RepoFactory) Proxy.newProxyInstance(RepoFactory.class.getClassLoader(), new Class[] {RepoFactory.class}, new InvocationHandler() {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return SpringManager.getBean(method.getReturnType());
		}
	});
}








