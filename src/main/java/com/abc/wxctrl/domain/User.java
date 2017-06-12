package com.abc.wxctrl.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.abc.wxctrl.utility.db.domain.IdDomain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_user")
@Entity
public class User extends IdDomain {
	
	public static final String USER_AUTHORITY = "WX_USER";

	/**
	 * 用户名
	 */
	@Column(name = "username", length = 20, unique = true)
	private String username;
	/**
	 * 密码
	 */
	@Column(name = "password", length = 50)
	private String password;
	/**
	 * 拥有的微信账号
	 */
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<WxAccount> wxAccounts = new ArrayList<>();
}





