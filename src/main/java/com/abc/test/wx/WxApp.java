package com.abc.test.wx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Primary;

import lombok.Cleanup;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

import com.abc.test.Application;
import com.abc.test.service.WxPersistenceService;
import com.abc.test.utility.SpringManager;
import com.abc.test.utility.UserManager;

@Log4j2
public class WxApp {
	
	@Primary
	public static void main(String[] args) throws IOException {
		OutputStream out = new FileOutputStream("d://test//out.txt", true);
		TeeOutputStream teeOutputStream = new TeeOutputStream(System.out, out);
		System.setOut( new PrintStream(teeOutputStream));
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
		SpringApplication application = builder.build();
		application.setWebEnvironment(false);
		application.run(args);
		
		WxMeta meta = start();
		Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + meta.getFile_qrCode()});
	}
	
	public static WxMeta start() {
		final WxMeta meta = new WxMeta();
		meta.setOwnerId(UserManager.getUser().getId());

		meta.getHttpClient().getUUID();
		meta.getHttpClient().getQrCode();
		
		new Thread() {
			public void run() {
				meta.getHttpClient().waitForLogin();
				
				meta.getHttpClient().login();
				log.info("微信登录成功");
				
				log.info("微信初始化...");
				meta.getHttpClient().wxInit();
				log.info("微信初始化成功");
				SpringManager.getBean(WxPersistenceService.class).saveWxAccount(meta);
				
				log.info("开启状态通知...");
				meta.getHttpClient().openStatusNotify();
				log.info("开启状态通知成功");
				
				log.info("获取联系人...");
				meta.getHttpClient().getContact();
				
				log.info("获取联系人成功");
				log.info("共有 " + meta.getContactList().size() + " 位联系人");
				new WxMsgListener().start(meta);
			}
		}.start();

		return meta;
	}
}