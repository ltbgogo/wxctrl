package com.abc.test.wx;

import static com.abc.test.repository.RepoFactory.f;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import com.abc.test.config.AppConfigBean;
import com.abc.test.domain.User;
import com.abc.test.domain.WxAccount;
import com.abc.test.repository.RepoFactory;
import com.abc.test.utility.httpclient.CookieStore;
import com.abc.test.utility.httpclient.HttpClientConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Getter
@Setter
public class WxMeta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String ownerId;
	
	private String frontMsg;

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
	
	private File dir_root = new File("d://test/wx");
	private File file_qrCode = FileUtils.getFile(dir_root, "qrcode." + deviceId + ".jpg");
		
	// 微信联系人列表，可聊天的联系人列表
	private JSONArray memberList;
	private JSONArray contactList;
	private JSONArray groupList;
	
	private CookieStore cookieStore = new CookieStore();
	
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
	
	public User getOwner() {
		return RepoFactory.f.getUserRepo().findOne(this.getOwnerId());
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
	
	private WxMsgListener msgListener;
	public WxMsgListener getMsgListener() {
		if (this.msgListener == null) {
			this.msgListener = new WxMsgListener(this); 
		}
		return this.msgListener;
	}
}






