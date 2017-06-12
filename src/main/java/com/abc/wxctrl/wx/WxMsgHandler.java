package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import com.abc.wxctrl.utility.JsonUtil;
import com.alibaba.fastjson.JSONObject;

@Log4j
@AllArgsConstructor
public class WxMsgHandler implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private WxMeta meta;

	/**
	 * 处理消息
	 */
	public void handleMsg(JSONObject data) {
		if (data != null) {
			for (JSONObject msg : JsonUtil.toList(data.getJSONArray("AddMsgList"), JSONObject.class)) {
				String msgId = msg.getString("MsgId");
				if (rf.getWxFriendMsgRepo().findByMsgId(msgId) == null && rf.getWxGroupMsgRepo().findByMsgId(msgId) == null) {
					int msgType = msg.getIntValue("MsgType");
					//微信初始化消息
					if (msgType == 51) {
						log.info("成功截获微信初始化消息");
					} //文本消息 
					else if (msgType == 1) {
						this.persistMsg(msg);
					} //图片消息 
					else if (msgType == 3) {
						this.persistMsg(msg);
						meta.getHttpClient().webwxgetmsgimg(msgId);
					} else if (msgType == 34) {
//						webwxsendmsg(wechatMeta, "二蛋还不支持语音呢", msg.getString("FromUserName"));
					} else if (msgType == 42) {
//						log.info(name + " 给你发送了一张名片:");
						log.info("=========================");
					}
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












