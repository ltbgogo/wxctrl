package com.abc.wxctrl.manager;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.security.SecurityService;

public class UserManager {
	
	public static final String NAME_LOGIN_META = "login_meta";
	
	public static User getCurrent() {
		return rf.getUserRepo().findByUsername(SpringManager.getBean(SecurityService.class).getCurrent());
	}
	
	public static void setData(String name, Object value) {
		RequestContextHolder.currentRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getData(String name) {
		return (T) RequestContextHolder.currentRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
	}
	
	public static <T> T getData(String name, Class<T> requiredType) {
		return getData(name);
	}
	
	public static void removeData(String name) {
		RequestContextHolder.currentRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
	}
}



