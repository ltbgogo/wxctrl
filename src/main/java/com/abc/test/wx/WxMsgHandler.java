package com.abc.test.wx;

import static com.abc.test.repository.RepoFactory.f;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.abc.test.domain.WxMsg;
import com.abc.test.utility.JsonUtil;
import com.abc.test.utility.httpclient.HttpClient;
import com.abc.test.websocket.WxWebSocket;
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
			for (JSONObject msg : JsonUtil.toJsonObjects(data.get("AddMsgList"))) {
				if (f.getWxMsgRepo().findByMsgId(msg.getString("MsgId")) == null) {
					int msgType = msg.getIntValue("MsgType");
//					String name = getUserRemarkName(msg.getString("FromUserName"));
//					String content = msg.getString("Content");
					//微信初始化消息
					if (msgType == 51) {
						log.info("成功截获微信初始化消息");
					} //文本消息发送接收 
					else if (msgType == 1) {
						if (WxConst.FILTER_USERS.contains(msg.getString("ToUserName"))) {
							continue;
						} //我发送的消息 
						else if (msg.getString("FromUserName").equals(meta.getUser().getString("UserName"))) {
							//我发的群消息
							if (msg.getString("ToUserName").startsWith("@@")) {
								this.handleMe2Grp(msg);	
							} //我发给个人的消息 
							else {
								this.handleMe2Contact(msg);
							}
						} //别人发送的群消息 
						else if (msg.getString("FromUserName").startsWith("@@")) {
							this.handleOther2Grp(msg);						
						} //别人发送给我的消息
						else if (msg.getString("FromUserName").matches("@[^@].+")) {
							this.handleContact2Me(msg);	
						}
//						else if (msg.getString("ToUserName").indexOf("@@") != -1) {
//							String[] peopleContent = content.split(":<br/>");
//							log.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
//						} else {
//							log.info(name + ": " + content);
//							String ans = robot.talk(content);
//							webwxsendmsg(wechatMeta, ans, msg.getString("FromUserName"));
//							log.info("自动回复 " + ans);
//						}
					} else if (msgType == 3) {
						String msgId = msg.getString("MsgId");
						@Cleanup
						HttpClient c = meta.getHttpClient().createHttpClient(meta.getBase_uri() + "/webwxgetmsgimg");
						c.getQueryMap().put("MsgID", msgId);
						c.getQueryMap().put("skey", meta.getSkey());
						c.getQueryMap().put("type", "slave");
						c.connect();
						File imagePath = FileUtils.getFile(meta.getQrCodeImg(), "image", msgId + ".jpg");
						imagePath.getParentFile().mkdirs();
						log.info(imagePath);
//						webwxsendmsg(wechatMeta, "二蛋还不支持图片呢", msg.getString("FromUserName"));
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
	
	
	
}






