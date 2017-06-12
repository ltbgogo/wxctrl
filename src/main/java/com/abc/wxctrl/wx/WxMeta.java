package com.abc.wxctrl.wx;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.io.File;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import com.abc.wxctrl.config.AppConfigBean;
import com.abc.wxctrl.domain.User;
import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.manager.SpringManager;
import com.abc.wxctrl.utility.JsonUtil;
import com.abc.wxctrl.utility.httpclient.CookieStore;
import com.abc.wxctrl.utility.httpclient.HttpClientConfig;
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
	private JSONObject friends = new JSONObject();
	private JSONObject groups = new JSONObject();
	
	private CookieStore cookieStore = new CookieStore();
	
	//当前元数据的状态
	private transient WxMetaStatus metaStatus = WxMetaStatus.newMeta;
	
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
		WxAccount wxAccount = rf.getWxAccountRepo().findByUin(this.getWxuin());
		if (wxAccount == null) {
			wxAccount = new WxAccount();
			wxAccount.setUin(this.getWxuin());
			wxAccount.setOwner(this.getOwner());
			wxAccount.setNickName(this.getUser().getString("NickName"));
			wxAccount = rf.getWxAccountRepo().save(wxAccount);
		}
		return wxAccount;
	}
	
	/**
	 * 账号拥有者
	 */
	public User getOwner() {
		return rf.getUserRepo().findOne(this.getOwnerId());
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

	public WxDBService getDbService() {
		return SpringManager.getBean(WxDBService.class);
	}
	
	private transient WxMsgListener msgListener;
	public WxMsgListener getMsgListener() {
		if (this.msgListener == null) {
			this.msgListener = new WxMsgListener(this); 
		}
		return this.msgListener;
	}
	
	public WxMsg2DB getMsg2DB() {
		WxMsg2DB msg2db = SpringManager.getBean(WxMsg2DB.class);
		msg2db.setMeta(this);
		return msg2db;
	}
	
	public static enum WxMetaStatus {
		newMeta, waitForLogin, loginSuccess, destroied;
	}
}






