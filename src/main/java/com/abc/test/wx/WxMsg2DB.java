package com.abc.test.wx;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import com.abc.test.domain.WxMsg;
import com.abc.test.utility.JsonUtil;
import com.alibaba.fastjson.JSONObject;

@AllArgsConstructor
public class WxMsg2DB {

	private WxMeta meta;

	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(JSONObject msg) {
		int msgType = msg.getIntValue("MsgType");
		String tmpGrpName = msg.getString("ToUserName");
		JSONObject grpData = meta.getHttpClient().getGrpInfo(tmpGrpName);
		String groupNickName = grpData.getString("NickName");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
		
		//将消息持久化到数据库
		this.handleMe2Grp(msg.getString("MsgId"), msgType, groupNickName, content, createTime);
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(JSONObject msg) {
		String tmpFromUserName = msg.getString("FromUserName");
		JSONObject fromUserInfo = JsonUtil.searchObject(meta.getContactList(), "UserName", tmpFromUserName);
		String fromUserNickName = fromUserInfo.getString("NickName");
		int msgType = msg.getIntValue("MsgType");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
		
		//将消息持久化到数据库
		this.handleContact2Me(msg.getString("MsgId"), msgType, fromUserNickName, content, createTime);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(JSONObject msg) {
		String tmpToUserName = msg.getString("ToUserName");
		JSONObject toUserInfo = JsonUtil.searchObject(meta.getContactList(), "UserName", tmpToUserName);
		String contactNickName = toUserInfo.getString("NickName");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
		int msgType = msg.getIntValue("MsgType");

		this.handleMe2Contact(msg.getString("MsgId"), msgType, contactNickName, content, createTime);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(JSONObject msg) {
		String tmpGrpName = msg.getString("FromUserName");
		int msgType = msg.getIntValue("MsgType");		
		String tmpSenderName = StringUtils.substringBefore(msg.getString("Content"), ":");
		String realContent = StringUtils.substringAfter(msg.getString("Content"), ">");
		JSONObject grpData = meta.getHttpClient().getGrpInfo(tmpGrpName);
		JSONObject senderInfo = JsonUtil.searchObject(grpData.getJSONArray("MemberList"), "UserName", tmpSenderName);
		String contactNickName = senderInfo.getString("NickName");
		String groupNickName = grpData.getString("NickName");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));

		//将消息持久化到数据库
		this.handleOther2Grp(msg.getString("MsgId"), msgType, groupNickName, contactNickName, realContent, createTime);
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(String msgId, int msgType, String fromUserNickName, String content, Date createTime) {
		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setWxAccount(meta.getWxAccount());
		wxMsg.setContent(msgType == 1 ? content : "");
		wxMsg.setMsgType(msgType);
		wxMsg.setFromContactNickName(fromUserNickName);
		wxMsg.setCreateTime(createTime);
		wxMsg.setMsgId(msgId);
		f.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(String msgId, int msgType, String groupNickName, String content, Date createTime) {
		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setWxAccount(meta.getWxAccount());
		wxMsg.setContent(msgType == 1 ? content : "");
		wxMsg.setMsgType(msgType);
		wxMsg.setCreateTime(createTime);
		wxMsg.setGroupNickName(groupNickName);
		wxMsg.setMsgId(msgId);
		f.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(String msgId, int msgType, String groupNickName, String contactNickName, String content, Date createTime) {
		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setWxAccount(meta.getWxAccount());
		wxMsg.setContent(msgType == 1 ? content : "");
		wxMsg.setCreateTime(createTime);
		wxMsg.setMsgType(msgType);
		wxMsg.setFromContactNickName(contactNickName);
		wxMsg.setGroupNickName(groupNickName);
		wxMsg.setMsgId(msgId);
		f.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(String msgId, int msgType, String contactNickName, String content, Date createTime) {
		//将消息持久化到数据库
		WxMsg wxMsg = new WxMsg();
		wxMsg.setWxAccount(meta.getWxAccount());
		wxMsg.setContent(msgType == 1 ? content : "");
		wxMsg.setToContactNickName(contactNickName);
		wxMsg.setCreateTime(createTime);
		wxMsg.setMsgType(msgType);
		wxMsg.setMsgId(msgId);
		f.getWxMsgRepo().save(wxMsg);
	}
}
