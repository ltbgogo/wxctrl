package com.abc.test.config;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties("appconfig")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfig {

	private String contextPath;
	private String name;
	private String version;
	
	public static final AppConfig INSTANCE = new AppConfig();
}
