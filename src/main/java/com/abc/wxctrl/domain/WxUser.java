package com.abc.wxctrl.domain;

import lombok.Getter;
import lombok.Setter;

import com.abc.utility.db.domain.IdDateDomain;
import com.abc.utility.db.domain.IdDomain;

/**
 * 登录用户
 */
@Getter
@Setter
public class WxUser extends IdDateDomain {

	/**
	 * 猜测应该是unique id
	 */
	private Integer uni;
	/**
	 * 昵称
	 */
	private String nickName;
}
