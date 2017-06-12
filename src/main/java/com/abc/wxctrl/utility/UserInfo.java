package com.abc.wxctrl.utility;

import lombok.Getter;
import lombok.Setter;

import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.repository.RepoFactory;
import com.abc.wxctrl.wx.WxMeta;

public class UserInfo {

	private String userId;
	@Setter
	@Getter
	private WxMeta loginMeta;
	
	public UserInfo(User user) {
		this.userId = user.getId();
	}
	
	public UserInfo(String userId) {
		this.userId = userId;
	}
	
	public User getUser() {
		return RepoFactory.rf.getUserRepo().findOne(this.userId);
	}
}



