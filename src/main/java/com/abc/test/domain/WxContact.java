package com.abc.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_wx_contact")
@Entity
public class WxContact extends IdDomain {
	
	private String userName; //"@b9b1f0d0f6523277fdcfb38ec3ba8ce1",
	private String nickName; //"安家邢台",
	private String headImgUrl; //"/cgi-bin/mmwebwx-bin/webwxgeticon?seq=662277073&username=@b9b1f0d0f6523277fdcfb38ec3ba8ce1&skey=@crypt_b26ec12f_25b03f7830a0209ccfac4a9388a2d53c",
	private String contactFlag; //3,
	private String memberCount; //0,
//	private String memberList; //[],
	private String remarkName; //"",
	private String hideInputBarFlag; //0,
	private String sex; //0,
	private String signature; //"安家要一套房，但房不等于家。有吐槽 有干货有态度 无节操",
	private String verifyFlag; //24,
	private String ownerUin; //0,
	private String pYInitial; //"AJXT",
	private String pYQuanPin; //"anjiaxingtai",
	private String remarkPYInitial; //"",
	private String remarkPYQuanPin; //"",
	private String starFriend; //0,
	private String appAccountFlag; //0,
	private String statues; //0,
	private String attrStatus; //0,
	private String province; //"河北",
	private String city; //"邢台",
	private String alias; //"anjiaxingtai",
	private String snsFlag; //0,
	private String uniFriend; //0,
	private String displayName; //"",
	private String chatRoomId; //0,
	private String keyWord; //"gh_",
	private String encryChatRoomId; //"",
	private String isOwner; //0
	
	/**
	 * 创建时间
	 */
	@Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	public static class ContactCategory {
		
	}
}





