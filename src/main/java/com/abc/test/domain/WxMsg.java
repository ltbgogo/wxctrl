package com.abc.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_msg")
@Entity
public class WxMsg {
	
	@Id
	@GeneratedValue
	private String id;
	
	@Column(name = "from_group_name", length = 50)
	private String groupName;
	
	@Column(name = "to_user_name", length = 50)
	private String toUserName;
	
	@Column(name = "from_user_name", length = 50)
	private String fromUserName;
	
	@Column(name = "msg_id", length = 50)
	private String msgId;
	
	@Lob
	private String content;
	
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
}



