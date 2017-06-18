package com.abc.wxctrl.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.manager.UserManager;

@Setter
@Getter
@ConfigurationProperties("appconfig")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfigBean {

	private String httpProxy;
	private String dataDirPath;
	
	public User getCurrent() {
		return UserManager.getCurrent();
	}
	
	public static final AppConfigBean INSTANCE = new AppConfigBean();
}
