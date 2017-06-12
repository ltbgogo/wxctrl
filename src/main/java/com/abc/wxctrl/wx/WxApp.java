package com.abc.wxctrl.wx;

import java.util.List;

import javax.transaction.Transactional;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.abc.wxctrl.WebApplication;
import com.abc.wxctrl.manager.SpringManager;
import com.abc.wxctrl.manager.UserManager;
import com.abc.wxctrl.utility.CollUtil;
import com.abc.wxctrl.utility.IOUtil;
import com.abc.wxctrl.utility.JsonUtil;
import com.abc.wxctrl.utility.RegexUtil;
import com.abc.wxctrl.wx.WxMeta.WxMetaStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Service
@Log4j
public class WxApp {
	
	public WxMeta startOne() {
		final WxMeta meta = new WxMeta();
		meta.setOwnerId(UserManager.getCurrent().getId());

		meta.getHttpClient().getUUID();
		meta.getHttpClient().getQrCode();
		
		new Thread() {
			public void run() {
				try {
					meta.setMetaStatus(WxMetaStatus.waitForLogin);
					
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
					meta.getHttpClient().getFriendContact();
					log.info("共有 " + meta.getFriends().size() + " 位联系人");
					
					log.info("选择同步线路");
					meta.getHttpClient().choiceSyncLine();
					
					log.info("获取群聊...");
					JSONObject data = meta.getHttpClient().webwxsync();
					String userNamesStr = data.getJSONArray("AddMsgList").getJSONObject(0).getString("StatusNotifyUserName");
					List<String> userNames = RegexUtil.match(userNamesStr, "@@[^,]+");
					meta.getHttpClient().batchGetContact(userNames);
					log.info("共有 " + meta.getGroups().size() + " 个群");
					
					meta.getMsgListener().start();
				} catch (Exception e) {
					meta.setMetaStatus(WxMetaStatus.destroied);
				}
			}
		}.start();

		return meta;
	}
}