package com.abc.test.wx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import com.abc.test.domain.User;
import com.abc.test.repository.RepoFactory;
import com.abc.test.utility.httpclient.CookieStore;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Getter
@Setter
public class WxMeta implements Serializable {

	private static final long serialVersionUID = 6395820832432929037L;
	
	private final WxHttpClient httpClient = new WxHttpClient(this);
	private final WxMsgHandler msgHandler = new WxMsgHandler(this);
	
	private String ownerId;
	
	private String status = "ok"; //ok failure 
	private String frontMsg;

	private String base_uri;
	private String redirect_uri;
	private String webpush_url;
	
	private String uuid;
	
	private String skey;
	private String synckeyStr;
	private String wxsid;
	private String wxuin;
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
}






