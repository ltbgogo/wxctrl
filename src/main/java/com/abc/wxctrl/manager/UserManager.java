package com.abc.wxctrl.manager;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Cleanup;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.security.SecurityService;
import com.abc.wxctrl.utility.Locker;

public class UserManager {
	
	private static final Map<String, Map<String, Object>> userDataStorageMap = Collections.synchronizedMap(new HashMap<String, Map<String,Object>>()); 
	
	public static User getCurrent() {
		return rf.getUserRepo().findByUsername(SpringManager.getBean(SecurityService.class).getCurrent());
	}
	
	private static Map<String, Object> getUserDataStorage() {
		String id = getCurrent().getId();
		if (userDataStorageMap.get(id) == null) {
			@Cleanup
			Locker locker = Locker.lock("user." + id);
			if (userDataStorageMap.get(id) == null) {
				userDataStorageMap.put(id, Collections.synchronizedMap(new HashMap<String, Object>()));
			}
		}
		return userDataStorageMap.get(id);
	}
	
	public static void setUserData(String key, Object value) {
		getUserDataStorage().put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getUserData(String key) {
		return (T) getUserDataStorage().get(key);
	}
	
	public static <T> T getUserData(String key, Class<T> requiredType) {
		return getUserData(key);
	}
	
	public static void removeUserData(String key) {
		getUserDataStorage().remove(key);
	}
	
	public static void setSessionData(String key, Object value) {
		RequestContextHolder.currentRequestAttributes().setAttribute(key, value, RequestAttributes.SCOPE_SESSION);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSessionData(String key) {
		return (T) RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_SESSION);
	}
	
	public static <T> T getSessionData(String key, Class<T> requiredType) {
		return getSessionData(key);
	}
	
	public static void removeSessionData(String key) {
		RequestContextHolder.currentRequestAttributes().removeAttribute(key, RequestAttributes.SCOPE_SESSION);
	}
	
	public static interface UserConst {
		String KEY_LOGIN_META = "login_meta";
	}
}



