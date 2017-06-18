package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.abc.wxctrl.domain.WxMsg;
import com.abc.wxctrl.domain.WxMsg.MsgType;
import com.abc.wxctrl.wx.WxMeta.WxMetaTemplate;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class WxMsg2DB extends WxMetaTemplate {
	
	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(JSONObject msg) {
		this.handleMe2Grp(msg.getString("MsgId"), 
				msg.getIntValue("MsgType"), 
				msg.getString("ToUserName"),
				msg.getString("Content"), 
				new Date(Long.parseLong(msg.getString("CreateTime") + "000")));
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(JSONObject msg) {
		this.handleContact2Me(msg.getString("MsgId"), 
				msg.getIntValue("MsgType"), 
				msg.getString("FromUserName"), 
				msg.getString("Content"), 
				new Date(Long.parseLong(msg.getString("CreateTime") + "000")));
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(JSONObject msg) {
		this.handleMe2Contact(msg.getString("MsgId"), 
				msg.getIntValue("MsgType"), 
				msg.getString("ToUserName"), 
				msg.getString("Content"), 
				new Date(Long.parseLong(msg.getString("CreateTime") + "000")));
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(JSONObject msg) {
		this.handleOther2Grp(msg.getString("MsgId"), 
				msg.getIntValue("MsgType"), 
				msg.getString("FromUserName"), 
				StringUtils.substringBefore(msg.getString("Content"), ":"), 
				StringUtils.substringAfter(msg.getString("Content"), ">"), 
				new Date(Long.parseLong(msg.getString("CreateTime") + "000")));
	}

	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(String msgId, int msgType, String groupUserName, String content, Date createTime) {
		WxMsg msg = this.initMsg(msgId, true, msgType, content, createTime);
		msg.setGroupName(meta.getGroups().getJSONObject(groupUserName).getString(WxConst.KEY_DISPLAY_NAME));
		rf.getWxMsgRepo().save(msg);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(String msgId, int msgType, String groupUserName, String senderUserName, String content, Date createTime) {
		WxMsg msg = this.initMsg(msgId, false, msgType, content, createTime);
		msg.setGroupName(meta.getGroups().getJSONObject(groupUserName).getString(WxConst.KEY_DISPLAY_NAME));
		//假如发送人也是自己的朋友
		if (meta.getFriends().getJSONObject(senderUserName) != null) {
			msg.setContactName(meta.getFriends().getJSONObject(senderUserName).getString(WxConst.KEY_DISPLAY_NAME));
		} else {
			String senderNickName = meta.getGroups().getJSONObject(groupUserName).getJSONObject(WxConst.KEY_MEMBERS_MAP).getJSONObject(senderUserName).getString(WxConst.KEY_NICKNAME);
			msg.setContactName(senderNickName);
		}
		rf.getWxMsgRepo().save(msg);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(String msgId, int msgType, String friendUserName, String content, Date createTime) {
		WxMsg msg = initMsg(msgId, true, msgType, content, createTime);
		msg.setContactName(meta.getFriends().getJSONObject(friendUserName).getString(WxConst.KEY_DISPLAY_NAME));
		rf.getWxMsgRepo().save(msg);
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(String msgId, int msgType, String friendUserName, String content, Date createTime) {
		WxMsg msg = initMsg(msgId, true, msgType, content, createTime);
		msg.setContactName(meta.getFriends().getJSONObject(friendUserName).getString(WxConst.KEY_DISPLAY_NAME));
		rf.getWxMsgRepo().save(msg);
	}
	
	private WxMsg initMsg(String msgId, boolean isMyEcho, int msgType, String content, Date createTime) {
		WxMsg msg = new WxMsg();
		msg.setMsgId(msgId);
		msg.setIsMyEcho(isMyEcho);
		msg.setCreateTime(createTime);
		msg.setAccount(meta.getWxAccount());
		msg.setMsgType(msgType);
		msg.setContent(content);
		//根据消息类型具体处理内容
		switch (MsgType.of(msgType)) {
		case imageMsg:
			meta.getHttpClient().webwxgetmsgimg(msgId);
			break;
		case voiceMsg:
			meta.getHttpClient().webwxgetvoice(msgId);
			break;
		case vedioMsg:
			meta.getHttpClient().webwxgetvideo(msgId);
			break;
		case animatedEmoticon:
			meta.getHttpClient().downloadEmotion(msg.getContent());
			break;
		default:
			break;
		}
		return msg;
	}
}
