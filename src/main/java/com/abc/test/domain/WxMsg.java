package com.abc.test.domain;

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

import com.abc.test.websocket.WxWebSocket;
import com.abc.test.wx.WxMetaStorage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_msg")
@Entity
public class WxMsg extends IdDomain {
	/**
	 * 消息所属微信号
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wx_account_id")
	@JsonIgnore
	private WxAccount wxAccount;
	/**
	 * 群名称
	 */
	@Column(name = "group_nick_name", length = 50)
	private String groupNickName;
	/**
	 * 接收人
	 */
	@Column(name = "to_contact_nick_name", length = 50)
	private String toContactNickName;
	/**
	 * 发送人
	 */
	@Column(name = "from_contact_nick_name", length = 50)
	private String fromContactNickName;
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
	 * MsgType	说明
		1	文本消息
		3	图片消息
		34	语音消息
		37	好友确认消息
		40	POSSIBLEFRIEND_MSG
		42	共享名片
		43	视频消息
		47	动画表情
		48	位置消息
		49	分享链接
		50	VOIPMSG
		51	微信初始化消息
		52	VOIPNOTIFY
		53	VOIPINVITE
		62	小视频
		9999	SYSNOTICE
		10000	系统消息
		10002	撤回消息
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
		WxWebSocket.sendMessage(this.getWxAccount().getId(), "sync");
	}
}



