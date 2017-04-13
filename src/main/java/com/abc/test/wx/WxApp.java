package com.abc.test.wx;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.abc.test.domain.WxAccount;

import static com.abc.test.repository.RepoFactory.f;

import com.abc.test.service.WxPersistenceService;
import com.abc.test.utility.SpringManager;
import com.abc.test.utility.UserManager;
import com.alibaba.fastjson.JSONObject;

@Log4j
public class WxApp {
	
	public static void main(String[] args) throws IOException {
		WxMeta meta = start();
		Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + meta.getFile_qrCode()});

	}
	
	public static WxMeta start() {
		final WxMeta meta = new WxMeta();
		meta.setOwnerId(UserManager.getUser().getId());
		
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
				SpringManager.getBean(WxPersistenceService.class).saveWxAccount(meta);
				
				log.info("开启状态通知...");
				service.openStatusNotify();
				log.info("开启状态通知成功");
				
				log.info("获取联系人...");
				service.getContact();
				log.info("获取联系人成功");
				log.info("共有 " + meta.getContactList().size() + " 位联系人");
				new WxMsgListener().start(meta);
			}
		}.start();

		return meta;
	}
}