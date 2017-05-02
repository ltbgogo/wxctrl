package com.abc.test.manager;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.abc.test.domain.User;
import com.abc.test.repository.RepoFactory;
import com.abc.test.utility.UserInfo;

public class UserManager {
	
	public static UserInfo getUserInfo() {
		//用于main测试环境
		if (RequestContextHolder.getRequestAttributes() == null) {
			return new UserInfo("anonymous");
		} else {
			UserInfo info = (UserInfo) RequestContextHolder.currentRequestAttributes().getAttribute("userinfo", RequestAttributes.SCOPE_SESSION);
			if (info == null) {
				info = new UserInfo("anonymous");
				setUserInfo(info);
			}
			return info;
		}
	}
	
	public static void setUserInfo(UserInfo info) {
		RequestContextHolder.currentRequestAttributes().setAttribute("userinfo", info, RequestAttributes.SCOPE_SESSION);
	}
}
