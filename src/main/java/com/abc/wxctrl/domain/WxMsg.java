package com.abc.wxctrl.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.abc.wxctrl.utility.db.domain.IdDomain;
import com.abc.wxctrl.websocket.WxWebSocket;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 注意，组名称和联系人名称优先来自于“备注名称”，然后是“昵称”。
 */
@Getter
@Setter
@Table(name = "t_wx_msg")
@Entity
public class WxMsg extends IdDomain {
	/**
	 * 消息所属微信账号
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	@JsonIgnore
	private WxAccount account;
	/**
	 * 群名称
	 */
	@Column(name = "group_name", length = 200)
	private String groupName;
	/**
	 * 联系人
	 */
	@Column(name = "contact_name", length = 200)
	private String contactName;
	/**
	 * 是否消息由“我”发出
	 */
	@Column(name = "is_my_echo")
	private Boolean isMyEcho;
	/**
	 * 消息编号
	 */
	@Column(name = "msg_id", length = 50, unique = true)
	private String msgId;
	/**
	 * 消息内容
	 */
	@Lob
	private String content;
	/**
	 * 消息类型
	 */
	@Column(name = "msg_type")
	private Integer msgType = 1;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@PostPersist
	protected void postPersist() {
		//发送“同步”通知
		WxWebSocket.sendMessage(this.getAccount().getId(), "sync");
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
		
		public final int flag;
		public final String text;
		
		public static MsgType of(int flag) {
			for (MsgType type : MsgType.values()) {
				if (type.flag == flag) {
					return type;
				}
			}
			throw new RuntimeException();
		}
	}
}



