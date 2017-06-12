package com.abc.wxctrl.utility.db.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.websocket.WxWebSocket;

@Getter
@Setter
@MappedSuperclass
public abstract class WxMsgDomain extends IdDomain {
	/**
	 * 消息编号
	 */
	@Column(name = "msg_id", length = 50)
	private String msgId;
	/**
	 * 是否是我发出的消息
	 */
	@Column(name = "is_my_echo")
	private Boolean isMyEcho;
	/**
	 * 消息内容
	 */
	@Lob
	private String content;
	/**
	 * 消息类型
	 */
	@Column(name = "msg_type")
	private Integer msgType = MsgType.textMsg.getFlag();
	/**
	 * 创建时间
	 */
	@Column(name = "create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	public abstract WxAccount getWxAccount(); 
	
	@PostPersist
	protected void postPersist() {
		//发送“同步”通知
		WxWebSocket.sendMessage(this.getWxAccount().getId(), "sync");
	}
	
	/**
	 * 消息类型
	 */
	@AllArgsConstructor
	@Getter
	public static enum MsgType {
		textMsg(1, "文本消息"),
		imageMsg(3, "图片消息"),
		voiceMsg(34, "语音消息"),
		confirmMsg(37, "好友确认消息"),
		possibleFriendMsg(40, "POSSIBLEFRIEND_MSG"),
		shareCard(42, "共享名片"),
		vedioMsg(43, "视频消息"),
		animatedEmoticon(47, "动画表情"),
		locationMsg(48, "位置消息"),
		sharedLinks(49, "分享链接"),
		voipMsg(50, "VOIPMSG"),
		initMsg(51, "微信初始化消息"),
		voipNotify(52, "VOIPNOTIFY"),
		voipInvite(53, "VOIPINVITE"),
		smallVideo(62, "小视频"),
		sysNotice(9999, "SYSNOTICE"),
		sysMsg(10000, "系统消息"),
		withdrawMsg(10002, "撤回消息	");
		private int flag;
		private String text;

	}
}



