package com.abc.test.wx;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.resources.SenderMessages;

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
				int msgType = msg.getIntValue("MsgType");
//				String name = getUserRemarkName(msg.getString("FromUserName"));
//				String content = msg.getString("Content");

				if (msgType == 51) {
					log.info("成功截获微信初始化消息");
				} else if (msgType == 1) {
					if (WxConst.FILTER_USERS.contains(msg.getString("ToUserName"))) {
						continue;
					} //我发送的消息 
					else if (msg.getString("FromUserName").equals(meta.getUser().getString("UserName"))) {
						//我发的群消息
						if (msg.getString("ToUserName").startsWith("@@")) {
							this.handleMe2Grp(msg);	
						} //我发给个人的消息 
						else {
							this.handleMe2Individual(msg);
						}
					} //别人发送的群消息 
					else if (msg.getString("FromUserName").startsWith("@@")) {
						this.handleOther2Grp(msg);						
					} else if (msg.getString("ToUserName").indexOf("@@") != -1) {
//						String[] peopleContent = content.split(":<br/>");
//						log.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
					} else {
//						log.info(name + ": " + content);
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
//					log.info(name + " 给你发送了一张名片:");
					log.info("=========================");
				}
			}
		}
	}
	
	/**
	 * 处理我发送到群里的消息
	 */
	private void handleMe2Grp(JSONObject msg) {
		String tmpGrpName = msg.getString("ToUserName");
		JSONObject grpData = meta.getHttpClient().getGrpInfo(tmpGrpName);
		String grpNickName = grpData.getString("NickName");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));

		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setContent(content);
		wxMsg.setCreateTime(createTime);
		wxMsg.setGroupName(grpNickName);
		wxMsg.setMsgId(msg.getString("MsgId"));
		f.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	private void handleMe2Individual(JSONObject msg) {
		String tmpToUserName = msg.getString("ToUserName");
		JSONObject toUserInfo = JsonUtil.search(meta.getMemberList(), "UserName", tmpToUserName);
		String toUserNickName = toUserInfo.getString("NickName");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));

		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setContent(content);
		wxMsg.setToUserName(toUserNickName);
		wxMsg.setCreateTime(createTime);
		wxMsg.setMsgId(msg.getString("MsgId"));
		f.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	private void handleOther2Grp(JSONObject msg) {
		String tmpGrpName = msg.getString("FromUserName");
		String tmpSenderName = StringUtils.substringBefore(msg.getString("Content"), ":");
		String realContent = StringUtils.substringAfter(msg.getString("Content"), ">");
		JSONObject grpData = meta.getHttpClient().getGrpInfo(tmpGrpName);
		JSONObject senderInfo = JsonUtil.search(grpData.getJSONArray("MemberList"), "UserName", tmpSenderName);
		String senderNickName = senderInfo.getString("NickName");
		String grpNickName = grpData.getString("NickName");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
	
		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setContent(realContent);
		wxMsg.setCreateTime(createTime);
		wxMsg.setFromUserName(senderNickName);
		wxMsg.setGroupName(grpNickName);
		wxMsg.setMsgId(msg.getString("MsgId"));
		f.getWxMsgRepo().save(wxMsg);
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
