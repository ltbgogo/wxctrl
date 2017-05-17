package com.abc.test.wx;

import static com.abc.test.repository.RepoFactory.f;

import java.io.File;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;

import com.abc.test.config.AppConfigBean;
import com.abc.test.domain.User;
import com.abc.test.domain.WxAccount;
import com.abc.test.utility.JsonUtil;
import com.abc.test.utility.httpclient.CookieStore;
import com.abc.test.utility.httpclient.HttpClientConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Getter
@Setter
public class WxMeta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String ownerId;
	
	private String base_uri;
	private String redirect_uri;
	private String webpush_url;
	private String uuid;
	
	private String skey;
	private String synckeyStr;
	private String wxsid;
	private String wxuin; //微信账号的唯一标志，要存储到数据库
	private String pass_ticket;
	private String deviceId = "e" + System.nanoTime();
		
	private JSONObject baseRequest;
	private JSONObject syncKey;
	private JSONObject user;
		
	// 微信联系人列表，可聊天的联系人列表
	private JSONArray contactList = new JSONArray();
	private JSONArray groupList = new JSONArray();
	
	private CookieStore cookieStore = new CookieStore();
	
	/**
	 * 微信登录码图片
	 */
	public File getQrCodeImg() {
		File qrCodeImg = FileUtils.getFile(WxConst.TMP_DIR, "qrcode", deviceId + ".jpg");
		qrCodeImg.getParentFile().mkdirs();
		return qrCodeImg;
	}
	
	/**
	 * 微信账号
	 */
	public WxAccount getWxAccount() {
		WxAccount wxAccount = f.getWxAccountRepo().findByUin(this.getWxuin());
		if (wxAccount == null) {
			wxAccount = new WxAccount();
			wxAccount.setUin(this.getWxuin());
			wxAccount.setOwner(this.getOwner());
			wxAccount.setNickName(this.getUser().getString("NickName"));
			wxAccount = f.getWxAccountRepo().save(wxAccount);
		}
		return wxAccount;
	}
	
	/**
	 * 账号拥有者
	 */
	public User getOwner() {
		return f.getUserRepo().findOne(this.getOwnerId());
	}
	
	public void setSyncKey(JSONObject syncKey) {
		this.syncKey = syncKey;
		StringBuffer synckey = new StringBuffer();
		JSONArray list = syncKey.getJSONArray("List");
		for (int i = 0, len = list.size(); i < len; i++) {
			JSONObject item = list.getJSONObject(i);
			synckey.append("|" + item.getIntValue("Key") + "_" + item.getIntValue("Val"));
		}
		this.setSynckeyStr(synckey.substring(1));
	}
	
	private transient WxHttpClient httpClient;
	public WxHttpClient getHttpClient() {
		if (this.httpClient == null) {
			HttpClientConfig config = new HttpClientConfig();
			config.getRequestHeaderMap().put("Host", "wx.qq.com");
			config.getRequestHeaderMap().put("Referer", "https://wx.qq.com/?&lang=zh_CN");
			config.setCookieStore(this.cookieStore);
			config.setHttpProxy(AppConfigBean.INSTANCE.getHttpProxy());
			config.setEnableSNIExtension(false);
			this.httpClient = new WxHttpClient(this, config); 
		}
		return this.httpClient;
	}
	
	private transient WxMsgHandler msgHandler;
	public WxMsgHandler getMsgHandler() {
		if (this.msgHandler == null) {
			this.msgHandler = new WxMsgHandler(this);
		}
		return msgHandler;
	}
	
	private transient WxMsgListener msgListener;
	public WxMsgListener getMsgListener() {
		if (this.msgListener == null) {
			this.msgListener = new WxMsgListener(this); 
		}
		return this.msgListener;
	}
	
	private transient WxMsg2DB msg2db;
	public WxMsg2DB getMsg2DB() {
		if (this.msg2db == null) {
			this.msg2db = new WxMsg2DB(this); 
		}
		return this.msg2db;
	}
	
	public void addContact(JSONObject contact) {
		if (JsonUtil.searchObject(this.contactList, "UserName", contact.get("UserName")) == null) {
			this.contactList.add(contact);
		}
	}
	
	public void addGroup(JSONObject group) {
		if (JsonUtil.searchObject(this.groupList, "UserName", group.get("UserName")) == null) {
			this.groupList.add(group);
		}
	}
}






