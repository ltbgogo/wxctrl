package com.abc.test.wx;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import com.abc.test.WebApplication;
import com.abc.test.domain.User;
import com.abc.test.domain.WxAccount;
import com.abc.test.manager.SpringManager;
import com.abc.test.manager.UserManager;
import com.abc.test.utility.IOUtil;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Service
@Log4j
public class WxApp {
	
	@SneakyThrows
	public static void main(String[] args) {
		IOUtil.forkConsoleOut("d://test//out.txt");
		SpringManager.startMailApplication(WebApplication.class, args);
		
		WxApp app = SpringManager.getBean(WxApp.class);
		app.test();
	}
	
	@SneakyThrows
	public void test() {
		WxMeta meta = startOne();
		Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + meta.getFile_qrCode()});
	}
	
	public void restoreAllSessions() {
		for (User user : f.getUserRepo().findAll()) {
			this.restoreSession(user);
		}
	}
	
	public void restoreSession(User user) {
		for (WxAccount account : user.getWxAccounts()) {
			final WxMeta meta = account.getMeta(); 
			if (meta != null) {
				try {
					JSONObject syncStatus = meta.getHttpClient().syncCheck();	
					if (syncStatus.getIntValue("retcode") == 1102 ||
							syncStatus.getIntValue("retcode") == 1101) {
						account.setMeta(null);
						account.setIsOnline(false);
						f.getWxAccountRepo().save(account);
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								meta.getMsgListener().start();
							}
						}).start();
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public WxMeta startOne() {
		final WxMeta meta = new WxMeta();
		meta.setOwnerId(UserManager.getUserInfo().getUser().getId());

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
				
				log.info("开启状态通知...");
				meta.getHttpClient().openStatusNotify();
				log.info("开启状态通知成功");
				
				log.info("获取联系人...");
				meta.getHttpClient().getContact();
				
				log.info("获取联系人成功");
				log.info("共有 " + meta.getContactList().size() + " 位联系人");

				log.info("选择同步线路");
				meta.getHttpClient().choiceSyncLine();

				meta.getMsgListener().start();
			}
		}.start();

		return meta;
	}
}