package com.abc.wxctrl.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.abc.wxctrl.utility.db.domain.WxMsgDomain;

/**
 * 群聊
 */
@Getter
@Setter
@Table(name = "t_wx_group_msg")
@Entity
public class WxGroupMsg extends WxMsgDomain {

	/**
	 * 消息所在的群聊
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wx_group_id")
	private WxGroup wxGroup;
	/**
	 * 发送者你出
	 */
	private String senderNickName;
	/**
	 * 发送者是自己的朋友
	 */
	private WxFriend wxFriendSender;
	
	@Override
	public WxAccount getWxAccount() {
		return this.getWxGroup().getWxAccount();
	}	
}





