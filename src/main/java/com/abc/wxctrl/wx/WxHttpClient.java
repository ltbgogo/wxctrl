package com.abc.wxctrl.wx;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.abc.wxctrl.utility.CollUtil;
import com.abc.wxctrl.utility.DateUtil;
import com.abc.wxctrl.utility.JsonUtil;
import com.abc.wxctrl.utility.RegexUtil;
import com.abc.wxctrl.utility.httpclient.HttpClient;
import com.abc.wxctrl.utility.httpclient.HttpClientConfig;
import com.abc.wxctrl.wx.WxMeta.WxMetaStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@AllArgsConstructor
@Log4j
public class WxHttpClient {
	
	private WxMeta meta;
	private HttpClientConfig httpClientConfig;
	
	/**
	 * 下载消息图片
	 */
	@SneakyThrows
	public File webwxgetmsgimg(String msgId) {
		@Cleanup
		HttpClient c = meta.getHttpClient().createHttpClient(meta.getBase_uri() + "/webwxgetmsgimg");
		c.getQueryMap().put("MsgID", msgId);
		c.getQueryMap().put("skey", meta.getSkey());
		c.connect();
		File file = FileUtils.getFile(WxConst.DATA_MSG_IMG_DIR, msgId + ".jpg");
		file.getParentFile().mkdirs();
		FileUtils.copyInputStreamToFile(c.getResponseByStream(), file);
		log.info("保存消息图片：" + file);
		return file;
	}
	
	/**
	 * 获取群信息
	 */
	@SneakyThrows
	public void batchGetContact(List<String> userNames) {
		//分多次请求获取组信息
		for (List<String> subUserNames : CollUtil.group(userNames, WxConst.MAX_NUM_FOR_GRP_INFO)) {
			JSONArray contactList = meta.getHttpClient()._batchGetContact(subUserNames).getJSONArray("ContactList");
			//遍历组
			for (JSONObject contact : JsonUtil.toList(contactList, JSONObject.class)) {
				//假如有昵称，就说明这个组是有效的
				if (StringUtils.isNotBlank(contact.getString(WxConst.KEY_NICKNAME))) {
					//将群聊成员列表转成Map形式，键是UserName, 便于后面去成员信息
					JSONObject membersMap = JsonUtil.toMap(contact.getJSONArray("MemberList"), WxConst.KEY_USERNAME);
					contact.put("MemberList", membersMap);
					//获取seq信息
					String headImgUrl = contact.getString(WxConst.KEY_HEAD_IMG_URL);
					contact.put(WxConst.KEY_SEQ, Long.parseLong(StringUtils.substringBetween(headImgUrl, "seq=", "&")));
					//将组放到元数据中
					meta.getGroups().put(contact.getString(WxConst.KEY_USERNAME), contact);
					
					//同步群聊信息到数据库
					meta.getDbService().syncGroup(meta.getWxAccount(), contact);
				}
				//休眠，怕请求频繁被墙
				TimeUnit.SECONDS.sleep(1);
			}
		}
	}
	
	private JSONObject _batchGetContact(List<String> userNames) {
		@Cleanup
		HttpClient client = createHttpClient(meta.getBase_uri() + "/webwxbatchgetcontact");        
        client.getQueryMap().put("pass_ticket", this.meta.getPass_ticket());
        client.getQueryMap().put("type", "ex");
        client.getQueryMap().put("r", System.currentTimeMillis());
        client.getContentJSONObject().put("BaseRequest", this.meta.getBaseRequest());
        client.getContentJSONObject().put("Count", userNames.size());
        client.getContentJSONObject().put("List", new JSONArray());
        for (String tmpUserName : userNames) {
        	client.getContentJSONObject().getJSONArray("List").add(ArrayUtils.toMap(new String[][] {
        			{"UserName", tmpUserName}, {"ChatRoomId", ""}
        	}));
        }
        client.connect();
        return client.getResponseByJsonObject();
	}

	/**
	 * 获取朋友联系人
	 */
	public void getFriendContact() {
		@Cleanup
		HttpClient client = createHttpClient(meta.getBase_uri() + "/webwxgetcontact");
		client.getQueryMap().put("seq", 1);
		client.getQueryMap().put("pass_ticket", meta.getPass_ticket());
		client.getQueryMap().put("skey", meta.getSkey());
		client.getQueryMap().put("r", DateUtil.seconds());
		client.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		client.connect();
		
		JSONObject data = client.getResponseByJsonObject();
		this.validateRet(data, "获取联系人失败");
		for (Object o : data.getJSONArray("MemberList")) {
			JSONObject contact = (JSONObject) o;
			//公众号/服务号
			if ((contact.getIntValue("VerifyFlag") & 8) != 0) {
			} //特殊联系人
			else if (WxConst.FILTER_USERS.contains(contact.getString("UserName"))) {
			} //群聊
			else if (contact.getString("UserName").indexOf("@@") != -1) {
			} //自己
			else if (contact.getString("UserName").equals(meta.getUser().getString("UserName"))) {
			} //常规联系人 
			else {
				String headImgUrl = contact.getString(WxConst.KEY_HEAD_IMG_URL);
				///cgi-bin/mmwebwx-bin/webwxgeticon?seq=649556693&username=@eaecf81792d3166a9d5acea588831a1b&skey=@crypt_b26ec12f_e42db0adfd77d46ac34270982f8f188
				contact.put(WxConst.KEY_SEQ, Long.parseLong(StringUtils.substringBetween(headImgUrl, "seq=", "&")));
				this.meta.getFriends().put(contact.getString(WxConst.KEY_USERNAME), contact);
				
				//同步朋友信息到数据库
				meta.getDbService().syncFriend(meta.getWxAccount(), contact);
			}
		}
	}
	
	public HttpClient createHttpClient(String url) {
		return new HttpClient(url, this.httpClientConfig);
	}

	/**
	 * 获取UUID
	 */
	public void getUUID() {
		log.info("获取到uuid");
		@Cleanup
		HttpClient client = createHttpClient(WxConst.JS_LOGIN_URL);
		client.getQueryMap().put("appid", "wx782c26e4c19acffb");
		client.getQueryMap().put("fun", "new");
		client.getQueryMap().put("lang", "zh_CN");
		client.getQueryMap().put("_", DateUtil.seconds());
		client.connect();
		
		//window.QRLogin.code = 200; window.QRLogin.uuid = "QebhTea-mw=="; 
		JSONObject data = this.parseWxSpec(client.getResponseByString());
		meta.setUuid(data.getString("window.QRLogin.uuid"));
	}
	
	/**
	 * 显示二维码
	 */
	@SneakyThrows
	public void getQrCode() {
		@Cleanup
		HttpClient c = createHttpClient(WxConst.QRCODE_URL + meta.getUuid());
		c.getQueryMap().put("t", "webwx");
		c.getQueryMap().put("_", DateUtil.seconds());
		c.connect();
		
		FileUtils.copyInputStreamToFile(c.getResponseByStream(), meta.getQrCodeImg());
	}
	
	/**
	 * 等待登录
	 */
	@SneakyThrows
	public void waitForLogin() {
		int tip = 0;
		while (true) {
			@Cleanup
			HttpClient c = createHttpClient("https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login");
			c.getQueryMap().put("tip", tip);
			c.getQueryMap().put("uuid", meta.getUuid());
			c.getQueryMap().put("_", DateUtil.seconds());
			c.connect();
			
			JSONObject data = this.parseWxSpec(c.getResponseByString());
			int code = data.getIntValue("window.code");
			if (201 == code) {
				tip = 1;
				log.info("成功扫描,请在手机上点击确认以登录");
			} else if (code == 200) {
				log.info("正在登录...");
				meta.setRedirect_uri(data.getString("window.redirect_uri") + "&fun=new");
				meta.setBase_uri(StringUtils.substringBeforeLast(meta.getRedirect_uri(), "/"));
				break;
			} else if (code == 400) {
				this.meta.setMetaStatus(WxMetaStatus.destroied);
				throw new RuntimeException("登录超时");
			} else {
				log.info("扫描code=" + code);
			}
			TimeUnit.MICROSECONDS.sleep(25063);;
		}
	}
	
	/**
	 * 登录
	 */
	public void login() {
		@Cleanup
		HttpClient c = createHttpClient(meta.getRedirect_uri());
		c.connect();
		String data = c.getResponseByString();
		meta.setSkey(RegexUtil.matchFirstGroup(data, "<skey>(\\S+)</skey>"));
		meta.setWxsid(RegexUtil.matchFirstGroup(data, "<wxsid>(\\S+)</wxsid>"));
		meta.setWxuin(RegexUtil.matchFirstGroup(data, "<wxuin>(\\S+)</wxuin>"));
		meta.setPass_ticket(RegexUtil.matchFirstGroup(data, "<pass_ticket>(\\S+)</pass_ticket>"));

		meta.setBaseRequest(new JSONObject());
		meta.getBaseRequest() .put("Uin", meta.getWxuin());
		meta.getBaseRequest() .put("Sid", meta.getWxsid());
		meta.getBaseRequest() .put("Skey", meta.getSkey());
		meta.getBaseRequest() .put("DeviceID", meta.getDeviceId());
	}

	/**
	 * 打开状态提醒
	 */
	public void openStatusNotify() {
		@Cleanup
		HttpClient client = createHttpClient(meta.getBase_uri() + "/webwxstatusnotify");
		client.getQueryMap().put("lang", "zh_CN");
		client.getQueryMap().put("pass_ticket", meta.getPass_ticket());
		client.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		client.getContentJSONObject().put("Code", 3);
		client.getContentJSONObject().put("FromUserName", meta.getUser().getString("UserName"));
		client.getContentJSONObject().put("ToUserName", meta.getUser().getString("UserName"));
		client.getContentJSONObject().put("ClientMsgId", DateUtil.seconds());
		client.connect();
		
		JSONObject data = client.getResponseByJsonObject();
		this.validateRet(data, "状态通知开启失败");
	}
	
	private void validateRet(JSONObject data, String errorMsg) {
		try {
			if (data.getJSONObject("BaseResponse").getIntValue("Ret") != 0) {
				throw new RuntimeException(errorMsg + "==>" + data);
			}
		} catch (Exception e) {
			throw new RuntimeException(errorMsg + "==>" + data);
		}
	}

	/**
	 * 微信初始化
	 */
	public void wxInit() {
		@Cleanup
		HttpClient client = createHttpClient(meta.getBase_uri() + "/webwxinit");
		client.getQueryMap().put("r", DateUtil.seconds());
		client.getQueryMap().put("pass_ticket", meta.getPass_ticket());
		client.getQueryMap().put("skey", meta.getSkey());
		client.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		client.connect();
		
		JSONObject data = client.getResponseByJsonObject();
		this.validateRet(data, "微信初始化失败");
		meta.setSyncKey(data.getJSONObject("SyncKey"));
		meta.setUser(data.getJSONObject("User"));
	}

	/**
	 * 选择同步线路
	 */
	public void choiceSyncLine() {
		for(String syncUrl : WxConst.SYNC_HOST){
			try {
				meta.setWebpush_url("https://" + syncUrl + "/cgi-bin/mmwebwx-bin/synccheck");
				JSONObject res = this.syncCheck();
				if(res.getIntValue("retcode") == 0) {
					log.info(String.format("选择线路：[%s]", syncUrl));
					return;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		throw new RuntimeException("同步线路不通畅");
	}
	
	/**
	 * 检测心跳
	 */
	public JSONObject syncCheck() {
		@Cleanup
		HttpClient c = createHttpClient(meta.getWebpush_url());
		c.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		c.getQueryMap().put("r", System.nanoTime());
		c.getQueryMap().put("skey",	meta.getSkey());
		c.getQueryMap().put("uin", meta.getWxuin());
		c.getQueryMap().put("sid", meta.getWxsid());
		c.getQueryMap().put("deviceid", meta.getDeviceId()); 
		c.getQueryMap().put("synckey", meta.getSynckeyStr()); 
		c.getQueryMap().put("_", System.currentTimeMillis());
		
		System.out.println(meta.getWebpush_url());
		System.out.println(c.getQueryMap());
		c.connect();
		////window.synccheck={retcode:"0",selector:"0"}
		JSONObject data = this.parseWxSpec(c.getResponseByString()).getJSONObject("window.synccheck");
		System.out.println(data);
		//new int[] {data.getIntValue("retcode"), data.getIntValue("selector")};
		return data;
	}
	
	/**
	 * 发送消息
	 */
	public void webwxsendmsg(String msgContent, String to) {
		String clientMsgId = System.nanoTime() + "";
		JSONObject Msg = new JSONObject();
		//1应该是文本消息
		Msg.put("Type", 1);
		Msg.put("Content", msgContent);
		Msg.put("FromUserName", meta.getUser().getString("UserName"));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		@Cleanup
		HttpClient c = createHttpClient(meta.getBase_uri() + "/webwxsendmsg");
		c.getQueryMap().put("lang", "zh_CN");
		c.getQueryMap().put("pass_ticket", meta.getPass_ticket());
		c.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		c.getContentJSONObject().put("Msg", Msg);
		c.connect();
		
		log.info("发送消息..." + msgContent);
		
	}
	
	public JSONObject webwxsync(){
		@Cleanup
		HttpClient c = createHttpClient(meta.getBase_uri() + "/webwxsync");
		c.getQueryMap().put("skey", meta.getSkey());
		c.getQueryMap().put("sid", meta.getWxsid());
		c.getContentJSONObject().put("BaseRequest", meta.getBaseRequest());
		c.getContentJSONObject().put("SyncKey", meta.getSyncKey());
		c.getContentJSONObject().put("rr", DateUtil.seconds());
		c.connect();
		
		JSONObject data = c.getResponseByJsonObject();
		this.validateRet(data, "同步syncKey失败");
		meta.setSyncKey(data.getJSONObject("SyncKey"));
		
		return data;
	}
	
	/**
	 * 解析微信响应内容
	 */
	private JSONObject parseWxSpec(String s) {
		JSONObject itemMap = new JSONObject();
		for (String itemStr : StringUtils.split(s, ";")) {
			String key = StringUtils.substringBefore(itemStr, "=").trim();
			String value = StringUtils.substringAfter(itemStr, "=").trim();
			if (value.startsWith("\"")) {
				itemMap.put(key, value.replaceAll("^\"|\"$", ""));
			} else if (value.startsWith("{") || value.startsWith("[")) {
				itemMap.put(key, JSON.parse(value));
			} else {
				itemMap.put(key, Integer.valueOf(value));
			}
		}
		return itemMap;
	}
}
