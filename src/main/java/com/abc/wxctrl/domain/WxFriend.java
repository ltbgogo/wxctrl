package com.abc.wxctrl.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.abc.wxctrl.utility.db.domain.WxContactDomain;

/**
 * 朋友
 */
@Getter
@Setter
@Table(name = "t_wx_friend")
@Entity
public class WxFriend extends WxContactDomain {
	
}





