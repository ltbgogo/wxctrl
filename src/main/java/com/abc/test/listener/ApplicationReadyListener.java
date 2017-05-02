package com.abc.test.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.abc.test.wx.WxApp;

@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		//恢复所有session没用过期的微信回话
		event.getApplicationContext().getBean(WxApp.class).restoreAllSessions();
	}
}


