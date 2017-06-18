package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import com.abc.wxctrl.domain.WxMsg.MsgType;
import com.abc.wxctrl.utility.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Log4j
@AllArgsConstructor
public class WxMsgHandler {
		
	private WxMeta meta;

	/**
	 * 处理消息
	 */
	@SneakyThrows
	public void handleMsg(JSONObject data) {
		if (data == null) {
			return;
		}	
		//假如没有消息，就睡眠一段时间，具体消息为空的原因尚不清楚，发送语音就会出现
		JSONArray msgList = data.getJSONArray("AddMsgList");
		if (msgList.size() == 0) {
			TimeUnit.SECONDS.sleep(WxMetaStorage.size() * 30);
			return;
		}
		//遍历消息
		for (JSONObject msg : JsonUtil.toList(msgList, JSONObject.class)) {
			String msgId = msg.getString("MsgId");
			if (rf.getWxMsgRepo().findByMsgId(msgId) == null) {
				switch (MsgType.of(msg.getIntValue("MsgType"))) {
				case initMsg: 
					log.info("成功截获微信初始化消息");
					this.persistMsg(msg);
					break;
				default:
					this.persistMsg(msg);
					break;
				}
			}
		}
	}
	
	/**
	 * 持久化消息
	 */
	private void persistMsg(JSONObject msg) {
		//特殊账号不做处理
		if (WxConst.FILTER_USERS.contains(msg.getString("ToUserName"))) {
		} //我发送的消息 
		else if (msg.getString("FromUserName").equals(meta.getUser().getString("UserName"))) {
			//我发的群消息
			if (msg.getString("ToUserName").startsWith("@@")) {
				meta.getMsg2DB().handleMe2Grp(msg);	
			} //我发给个人的消息 
			else {
				meta.getMsg2DB().handleMe2Contact(msg);
			}
		} //别人发送的群消息 
		else if (msg.getString("FromUserName").startsWith("@@")) {
			meta.getMsg2DB().handleOther2Grp(msg);						
		} //别人发送给我的消息
		else if (msg.getString("FromUserName").matches("@[^@].+")) {
			meta.getMsg2DB().handleContact2Me(msg);	
		}
	}
}












