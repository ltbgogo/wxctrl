package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.wx.WxMeta.WxMetaStatus;
import com.alibaba.fastjson.JSONObject;

@Log4j
@AllArgsConstructor
public class WxMsgListener {

	private WxMeta meta;
	
	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//设置登录状态
					meta.setMetaStatus(WxMetaStatus.loginSuccess);
					//记录登录日期
					WxAccount account = meta.getWxAccount();
					account.setLastLoginDate(new Date());
					rf.getWxAccountRepo().save(account);
					//存储元数据信息
					WxMetaStorage.put(meta);
					//进入监听
					_start();
				} finally {
					meta.setMetaStatus(WxMetaStatus.destroied);
				}
			}
		}).start();;
	}
	
	@SneakyThrows
	public void _start() {
		log.info("进入消息监听模式 ...");
		while(true) {
			try {
				JSONObject syncStatus = meta.getHttpClient().syncCheck();
				if(syncStatus.getIntValue("retcode") == 1100){
					log.info("你在手机上登出了微信，再见");
					break;
				}else if(syncStatus.getIntValue("retcode") == 0) {
 					if(syncStatus.getIntValue("selector") == 2) {
						JSONObject data = meta.getHttpClient().webwxsync();
						meta.getMsgHandler().handleMsg(data);
					} else if(syncStatus.getIntValue("selector") == 6) {
						JSONObject data = meta.getHttpClient().webwxsync();
						meta.getMsgHandler().handleMsg(data);
					} else if(syncStatus.getIntValue("selector") == 7) {
//						playWeChat += 1;
//						log.info(String.format("你在手机上玩微信被我发现了 %s 次", playWeChat));
//						meta.getHttpClient().webwxsync();
					} else if(syncStatus.getIntValue("selector") == 3) {
//								continue;
					} else if(syncStatus.getIntValue("selector") == 0) {
//								continue;
					}
				} else if (syncStatus.getInteger("retcode") == 1102) {
					log.info("微信Cookie过期，请重新登录");
					break;
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}			
			log.info("等待10s...");
			TimeUnit.SECONDS.sleep(WxMetaStorage.size() * 10);
		}
	}	
}
