package com.abc.wxctrl.wx;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

public class WxConst {

	public static final String HTTP_OK = "200";
	public static final String BASE_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
	public static final String JS_LOGIN_URL = "https://login.weixin.qq.com/jslogin";
	public static final String QRCODE_URL = "https://login.weixin.qq.com/qrcode/";
	
	//一些常用键
	public static final String KEY_USERNAME = "UserName";
	public static final String KEY_HEAD_IMG_URL = "HeadImgUrl";
	public static final String KEY_SEQ = "seq";
	public static final String KEY_NICKNAME = "NickName";
	public static final String KEY_REMARKNAME = "RemarkName";
	public static final String KEY_MEMBERS_MAP = "MembersMap";
	public static final String KEY_DISPLAY_NAME = "DisplayName";	
	
	//每次获取组成员信息的数量
	public static final int MAX_NUM_FOR_GRP_MEM_INFO = 50;
	//每次获取群信息的数量
	public static final int MAX_NUM_FOR_GRP_INFO = 10;
	
	//目录定义
	public static final File TMP_DIR = FileUtils.getFile(FileUtils.getTempDirectory(), "wxctrl");
	public static final File DATA_DIR = FileUtils.getFile("e://wxctrl_data");
	//存储微信登陆码的目录，命名格式是：用户编号 + ".jpg"
	public static final File DATA_QRCODE_DIR = FileUtils.getFile(DATA_DIR, "qrcode");
	//以下文件存储路径根据msgId生成，采用三级目录模式
	public static final File DATA_MSG_IMG_DIR = FileUtils.getFile(DATA_DIR, "msg_img");
	public static final File DATA_MSG_VOICE_DIR = FileUtils.getFile(DATA_DIR, "msg_voice");
	public static final File DATA_MSG_VEDIO_DIR = FileUtils.getFile(DATA_DIR, "msg_vedio");
	//以下文件存储路径根据md5生成，采用三级目录模式
	public static final File DATA_MSG_EMOTION_DIR = FileUtils.getFile(DATA_DIR, "msg_emotion");
	
	//特殊用户 须过滤
	public static final List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", 
			"fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", 
			"readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", 
			"weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
			"notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", 
			"notification_messages");
	
	//同步线路
	public static final String[] SYNC_HOST = {
		"webpush.wx.qq.com",
		"webpush.weixin.qq.com",
		"webpush2.weixin.qq.com",
		"webpush.wechat.com",
		"webpush1.wechat.com",
		"webpush2.wechat.com",
		"webpush1.wechatapp.com"
	};
}
