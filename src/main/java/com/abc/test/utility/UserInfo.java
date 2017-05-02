package com.abc.test.utility;

import lombok.Getter;
import lombok.Setter;

import com.abc.test.domain.User;
import com.abc.test.repository.RepoFactory;
import com.abc.test.wx.WxMeta;

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
		return RepoFactory.f.getUserRepo().findOne(this.userId);
	}
}



