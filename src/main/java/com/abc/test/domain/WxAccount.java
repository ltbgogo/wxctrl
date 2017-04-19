package com.abc.test.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.annotation.Timed;

import com.abc.test.wx.WxMeta;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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
	 * 登录的cookie信息
	 */
	@Column(name = "meta_serial")
	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] metaSerial;
	
	@SneakyThrows
	public void setMeta(WxMeta meta) {
		//序列化对象
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
		oos.writeObject(meta);    //写入customer对象
		this.setMetaSerial(byteArrayOutputStream.toByteArray());
	}
	
	@SneakyThrows
	public WxMeta getMeta() {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(this.getMetaSerial()));
		return (WxMeta) ois.readObject();
	}
	
}









