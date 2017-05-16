package com.abc.test.web;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMetaStorage;

public class CustomApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		//恢复所有session没用过期的微信回话
		WxMetaStorage.restoreFromDiskCache();
	}
}


