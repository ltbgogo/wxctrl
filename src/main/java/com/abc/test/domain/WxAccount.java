package com.abc.test.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import com.abc.test.wx.WxMeta;

@Getter
@Setter
@Table(name = "t_wx_account")
@Entity
public class WxAccount extends IdDomain {
	
	/**
	 * 微信用户编号，wxuin
	 */
	private String uin;
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
	/**
	 * 是否离线
	 */
	@Column(name = "is_online")
	private Boolean isOnline;

}









