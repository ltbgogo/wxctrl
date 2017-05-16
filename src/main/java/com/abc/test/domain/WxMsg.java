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
	@Column(name = "from_group_name", length = 50)
	private String groupName;
	/**
	 * 接收人
	 */
	@Column(name = "to_user_name", length = 50)
	private String toUserName;
	/**
	 * 发送人
	 */
	@Column(name = "from_user_name", length = 50)
	private String fromUserName;
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
	 * 创建时间
	 */
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@PostPersist
	protected void asd() {
		///WxWebSocket.sendMessage(meta.getWxuin(), "sync");
	}
}



