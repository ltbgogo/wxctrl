package com.abc.test.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_user")
@Entity
public class User extends IdDomain {

	/**
	 * 用户名
	 */
	@Column(name = "user_name", length = 20)
	private String userName;
	/**
	 * 密码
	 */
	@Column(name = "password", length = 20)
	private String password;
	/**
	 * 拥有的微信账号
	 */
	@OneToMany(mappedBy = "owner")
	private List<WxAccount> wxAccounts = new ArrayList<>();
}





