package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.util.Date;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.abc.wxctrl.domain.WxFriendMsg;
import com.abc.wxctrl.utility.db.domain.WxMsgDomain;
import com.alibaba.fastjson.JSONObject;

@Transactional
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class WxMsg2DB {

	@Setter
	private WxMeta meta;

	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(JSONObject msg) {
//		int msgType = msg.getIntValue("MsgType");
//		String tmpGrpName = msg.getString("ToUserName");
//		JSONObject grpData = meta.getGroups().getJSONObject(tmpGrpName);
//		String groupNickName = grpData.getString("NickName");
//		String content = msg.getString("Content");
//		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
//		
//		//将消息持久化到数据库
//		this.handleMe2Grp(msg.getString("MsgId"), msgType, groupNickName, content, createTime);
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(JSONObject msg) {
//		String tmpFromUserName = msg.getString("FromUserName");
//		JSONObject fromUserInfo = meta.getFriends().getJSONObject(tmpFromUserName);
//		String fromUserNickName = fromUserInfo.getString("NickName");
//		int msgType = msg.getIntValue("MsgType");
//		String content = msg.getString("Content");
//		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
//		
//		//将消息持久化到数据库
//		this.handleContact2Me(msg.getString("MsgId"), msgType, fromUserNickName, content, createTime);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(JSONObject msg) {
		String toUserName = msg.getString("ToUserName");
		String content = msg.getString("Content");
		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
		int msgType = msg.getIntValue("MsgType");
		this.handleMe2Contact(msg.getString("MsgId"), msgType, toUserName, content, createTime);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(JSONObject msg) {
//		String tmpGrpName = msg.getString("FromUserName");
//		int msgType = msg.getIntValue("MsgType");		
//		String tmpSenderName = StringUtils.substringBefore(msg.getString("Content"), ":");
//		String realContent = StringUtils.substringAfter(msg.getString("Content"), ">");
//		JSONObject grpData = meta.getGroups().getJSONObject(tmpGrpName);
//		JSONObject senderInfo = JsonUtil.searchObject(grpData.getJSONArray("MemberList"), "UserName", tmpSenderName);
//		String contactNickName = senderInfo.getString("NickName");
//		String groupNickName = grpData.getString("NickName");
//		Date createTime = new Date(Long.parseLong(msg.getString("CreateTime") + "000"));
//
//		//将消息持久化到数据库
//		this.handleOther2Grp(msg.getString("MsgId"), msgType, groupNickName, contactNickName, realContent, createTime);
	}
	
	/**
	 * 处理个人发送给我的消息
	 */
	public void handleContact2Me(String msgId, int msgType, String fromUserNickName, String content, Date createTime) {
//		//将消息持久化到数据库
//		WxFriendMsg msg = new WxFriendMsg();
//		msg.setContent(content);
//		
//		WxMsg wxMsg = new WxMsg();
//		wxMsg.setWxAccount(meta.getWxAccount());
//		wxMsg.setContent(msgType == 1 ? content : "");
//		wxMsg.setMsgType(msgType);
//		wxMsg.setFromContactNickName(fromUserNickName);
//		wxMsg.setCreateTime(createTime);
//		wxMsg.setMsgId(msgId);
//		rf.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理我发送到群里的消息
	 */
	public void handleMe2Grp(String msgId, int msgType, String groupNickName, String content, Date createTime) {
//		//将消息持久化到数据库
//		WxMsg wxMsg = new WxMsg();
//		wxMsg.setWxAccount(meta.getWxAccount());
//		wxMsg.setContent(msgType == 1 ? content : "");
//		wxMsg.setMsgType(msgType);
//		wxMsg.setCreateTime(createTime);
//		wxMsg.setGroupNickName(groupNickName);
//		wxMsg.setMsgId(msgId);
//		rf.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理其他人发送到群里的消息
	 */
	public void handleOther2Grp(String msgId, int msgType, String groupUserName, String senderUserName, String content, Date createTime) {
//		//将消息持久化到数据库
//		WxGroupMsg msg = initMsg(new WxGroupMsg(), false, msgType, content, createTime);
//		msg.setWxGroup();
//		msg.setSenderNickName(senderNickName);
//		wxMsg.setWxAccount(meta.getWxAccount());
//		wxMsg.setContent(msgType == 1 ? content : "");
//		wxMsg.setCreateTime(createTime);
//		wxMsg.setMsgType(msgType);
//		wxMsg.setFromContactNickName(contactNickName);
//		wxMsg.setGroupNickName(groupNickName);
//		wxMsg.setMsgId(msgId);
//		rf.getWxMsgRepo().save(wxMsg);
	}
	
	/**
	 * 处理我发送给个人的消息
	 */
	public void handleMe2Contact(String msgId, int msgType, String friendUserName, String content, Date createTime) {
		//将消息持久化到数据库
		WxFriendMsg msg = initMsg(new WxFriendMsg(), msgId, true, msgType, content, createTime);
		long seq = meta.getFriends().getJSONObject(friendUserName).getLong(WxConst.KEY_SEQ);
		msg.setWxFriend(rf.getWxFriendRepo().findByWxAccountAndSeq(meta.getWxAccount(), seq));
		rf.getWxFriendMsgRepo().save(msg);
	}
	
	private <T extends WxMsgDomain> T initMsg(T msg, String msgId, boolean isMyEcho, int msgType, String content, Date createTime) {
		msg.setMsgId(msgId);
		msg.setMsgType(msgType);
		msg.setContent(content);
		msg.setCreateDate(createTime);
		msg.setIsMyEcho(isMyEcho);
		return msg;
	}
}
