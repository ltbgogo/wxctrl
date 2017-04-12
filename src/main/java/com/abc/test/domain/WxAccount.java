package com.abc.test.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "t_wx_account")
public class WxAccount {

	@Id
	@GeneratedValue
	private String id;
	/**
	 * 微信用户编号
	 */
	private Integer uin;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 拥有者
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;
}





