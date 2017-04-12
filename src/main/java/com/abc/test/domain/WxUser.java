package com.abc.test.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "t_wx_user")
public class WxUser {
	
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
}


