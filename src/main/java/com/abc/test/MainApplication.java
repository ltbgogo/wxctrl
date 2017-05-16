package com.abc.test;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import com.abc.test.WebApplication;
import com.abc.test.config.AppConfigBean;
import com.abc.test.domain.User;
import com.abc.test.domain.WxAccount;
import com.abc.test.manager.SpringManager;
import com.abc.test.manager.UserManager;
import com.abc.test.utility.IOUtil;
import com.abc.test.utility.JsonUtil;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Service
@Log4j
public class MainApplication {
	
	@SneakyThrows
	public static void main(String[] args) {
//		IOUtil.forkConsoleOut("d://test//out.txt");
		SpringManager.startMailApplication(WebApplication.class, args);
		System.out.println("*****" + SpringManager.getBean(AppConfigBean.class).getHttpProxy());
		
//		MainApplication app = SpringManager.getBean(MainApplication.class);
//		app.run();
	}
	
	@SneakyThrows
	public void run() {
		WxAccount account = f.getWxAccountRepo().findOne("1");
		List<String> grpNames = f.getWxMsgRepo().findGroupNames(account);
		System.out.println(JsonUtil.toPrettyJson(grpNames));
		
		grpNames = f.getWxMsgRepo().findToUserName(account);
		System.out.println(JsonUtil.toPrettyJson(grpNames));
		
		grpNames = f.getWxMsgRepo().findFromUserName(account);
		System.out.println(JsonUtil.toPrettyJson(grpNames));
		
	}
}




