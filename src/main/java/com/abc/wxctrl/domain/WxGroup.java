package com.abc.wxctrl.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.abc.wxctrl.utility.db.domain.IdDomain;
import com.abc.wxctrl.utility.db.domain.WxContactDomain;
import com.abc.wxctrl.wx.WxConst;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_group")
@Entity
public class WxGroup extends WxContactDomain {
	
}





