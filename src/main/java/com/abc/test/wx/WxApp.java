package com.abc.test.wx;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

@Log4j
public class WxApp {
	
	public static WxMeta start() {
		final WxMeta meta = new WxMeta();
		
		final WxService service = new WxService(meta);
		
		service.getUUID();
		service.getQrCode();
		
		new Thread() {
			public void run() {
				service.waitForLogin();
				
				service.login();
				log.info("微信登录成功");
				
				log.info("微信初始化...");
				service.wxInit();
				log.info("微信初始化成功");
				
				log.info("开启状态通知...");
				service.openStatusNotify();
				log.info("开启状态通知成功");
				
				log.info("获取联系人...");
				service.getContact();
				log.info("获取联系人成功");
				log.info("共有 " + meta.contactList.size() + " 位联系人");
				new WxMsgListener().start(meta);
			}
		}.start();

		return meta;
	}
}