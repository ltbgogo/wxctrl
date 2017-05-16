package com.abc.test.wx;

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
	public static final File TMP_DIR = FileUtils.getFile(FileUtils.getTempDirectory(), "wxctrl");
	
	// 特殊用户 须过滤
	public static final List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", 
			"fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", 
			"readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", 
			"weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
			"notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", 
			"notification_messages");
	
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
