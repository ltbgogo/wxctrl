package com.abc.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.annotation.Timed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_account")
@Entity
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
	/**
	 * 最后登录时间
	 */
	@Column(name = "last_login_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginDate;
	/**
	 * 最后登出时间
	 */
	@Column(name = "last_logout_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date lastLogoutDate;
}





