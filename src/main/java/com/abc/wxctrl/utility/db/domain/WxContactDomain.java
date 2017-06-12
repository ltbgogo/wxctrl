package com.abc.wxctrl.utility.db.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.utility.ShortUUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class WxContactDomain extends IdDomain {
	
	/***
	 * 序列号，目前测试该序列号可作为账号中联系人的标识符
	 */
	@Column(unique = true)
	private Long seq;
	/**
	 * 昵称
	 */
	private String nickName;
	/***
	 * 备注名
	 */
	private String remarkName;
	/**
	 * 所属微信账号
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wx_account_id")
	private WxAccount wxAccount;
	/**
	 * 创建时间
	 */
	@Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	/**
	 * 返回屏幕显示名称
	 */
	public String getDisplayName() {
		return StringUtils.defaultIfBlank(this.getRemarkName(), this.getNickName());
	}
	
	@PrePersist
	protected void prePersist() {
		super.prePersist();
		if (this.getCreateDate() == null) {
			this.setCreateDate(new Date());
		}
	}
}
