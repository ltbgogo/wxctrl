package com.abc.wxctrl.repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.abc.wxctrl.manager.SpringManager;

/**
 * 数据仓库工程
 */
public interface RepoFactory {

	WxAccountRepository getWxAccountRepo();
	UserRepository getUserRepo();
	WxMsgRepository getWxMsgRepo();
	
	RepoFactory rf = (RepoFactory) Proxy.newProxyInstance(RepoFactory.class.getClassLoader(), new Class[] {RepoFactory.class}, new InvocationHandler() {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return SpringManager.getBean(method.getReturnType());
		}
	});
}








