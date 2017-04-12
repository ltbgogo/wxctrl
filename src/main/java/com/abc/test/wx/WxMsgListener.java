package com.abc.test.wx;

import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import com.alibaba.fastjson.JSONObject;

@Log4j
public class WxMsgListener {
	
	int playWeChat = 0;
	
	@SneakyThrows
	public void start(final WxMeta meta){
		WxService service = new WxService(meta);
		log.info("进入消息监听模式 ...");
		service.choiceSyncLine();
		while(true) {
			try {
				int[] arr = service.syncCheck();
				log.info(String.format("retcode=%s, selector=%s", arr[0], arr[1]));
				
				if(arr[0] == 1100){
					log.info("你在手机上登出了微信，再见");
					break;
				}
				if(arr[0] == 0) {
					if(arr[1] == 2) {
						JSONObject data = service.webwxsync();
						service.handleMsg(data);
					} else if(arr[1] == 6){
						JSONObject data = service.webwxsync();
						service.handleMsg(data);
					} else if(arr[1] == 7){
						playWeChat += 1;
						log.info(String.format("你在手机上玩微信被我发现了 %s 次", playWeChat));
						service.webwxsync();
					} else if(arr[1] == 3){
//								continue;
					} else if(arr[1] == 0){
//								continue;
					}
				} else {
					// 
				}	
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}			
			log.info("等待5s...");
			TimeUnit.SECONDS.sleep(5);
		}
	}	
}
