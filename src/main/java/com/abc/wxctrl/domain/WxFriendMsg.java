package com.abc.wxctrl.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.abc.wxctrl.utility.db.domain.IdDomain;
import com.abc.wxctrl.utility.db.domain.WxContactDomain;
import com.abc.wxctrl.utility.db.domain.WxMsgDomain;
import com.abc.wxctrl.wx.WxConst;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_friend_msg")
@Entity
@NoArgsConstructor
public class WxFriendMsg extends WxMsgDomain {
	
	public WxFriendMsg(WxFriend wxFriend) {
		this.wxFriend = wxFriend;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wx_friend_id")
	private WxFriend wxFriend;
	
	@Override
	public WxAccount getWxAccount() {
		return this.getWxFriend().getWxAccount();
	}	
}





