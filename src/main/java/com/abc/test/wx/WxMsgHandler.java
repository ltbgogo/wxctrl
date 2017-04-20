package com.abc.test.wx;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import static com.abc.test.repository.RepoFactory.f;

import com.abc.test.domain.WxMsg;
import com.abc.test.utility.JsonUtil;
import com.abc.test.utility.httpclient.HttpClient;
import com.alibaba.fastjson.JSONObject;

@Log4j
@AllArgsConstructor
public class WxMsgHandler implements Serializable {
	
	private WxMeta meta;

	/**
	 * 处理消息
	 */
	public void handleMsg(JSONObject data) {
		System.out.println("*********************" + data);
		if (data != null) {
			for (JSONObject msg : JsonUtil.toJsonObjects(data.get("AddMsgList"))) {
				int msgType = msg.getIntValue("MsgType");
				String name = getUserRemarkName(msg.getString("FromUserName"));
				String content = msg.getString("Content");

				if (msgType == 51) {
					log.info("成功截获微信初始化消息");
				} else if (msgType == 1) {
					if (WxConst.FILTER_USERS.contains(msg.getString("ToUserName"))) {
						continue;
					} else if (msg.getString("FromUserName").equals(meta.getUser().getString("UserName"))) {
						continue;
					} //群消息 
					else if (msg.getString("FromUserName").startsWith("@@")) {
						String fromUserName = msg.getString("FromUserName");
						String senderUserName = StringUtils.substringBefore(msg.getString("Content"), ":");
						String realContent = StringUtils.substringAfter(msg.getString("Content"), ">");
//						WxMsg m = new WxMsg();
						meta.getHttpClient().batchGetContact(Arrays.asList(fromUserName, senderUserName));
						
					} else if (msg.getString("ToUserName").indexOf("@@") != -1) {
						String[] peopleContent = content.split(":<br/>");
						log.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
					} else {
						log.info(name + ": " + content);
//						String ans = robot.talk(content);
//						webwxsendmsg(wechatMeta, ans, msg.getString("FromUserName"));
//						log.info("自动回复 " + ans);
					}
				} else if (msgType == 3) {
					String msgId = msg.getString("MsgId");
					@Cleanup
					HttpClient c = meta.getHttpClient().createHttpClient(meta.getBase_uri() + "/webwxgetmsgimg");
					c.getQueryMap().put("MsgID", msgId);
					c.getQueryMap().put("skey", meta.getSkey());
					c.getQueryMap().put("type", "slave");
					c.connect();
					File imagePath = FileUtils.getFile(meta.getDir_root(), "image", msgId + ".jpg");
					imagePath.getParentFile().mkdirs();
					log.info(imagePath);
//					webwxsendmsg(wechatMeta, "二蛋还不支持图片呢", msg.getString("FromUserName"));
				} else if (msgType == 34) {
//					webwxsendmsg(wechatMeta, "二蛋还不支持语音呢", msg.getString("FromUserName"));
				} else if (msgType == 42) {
					log.info(name + " 给你发送了一张名片:");
					log.info("=========================");
				}
			}
		}
	}
	
	private String getUserRemarkName(String id) {
		String name = "这个人物名字未知";
		for (JSONObject member : JsonUtil.toJsonObjects(meta.getMemberList())) {
			if (member.getString("UserName").equals(id)) {
				if (StringUtils.isNotBlank(member.getString("RemarkName"))) {
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}
}
