package com.abc.test.wx;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.abc.test.utility.DateUtil;
import com.abc.test.utility.JsonUtil;
import com.abc.test.utility.RegexUtil;
import com.abc.test.utility.httpclient.HttpClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@AllArgsConstructor
@Log4j
public class WxService {
	
	private WxMeta meta;

	/**
	 * 获取联系人
	 */
	public void getContact() {
		@Cleanup
		HttpClient client = createHttpClient(meta.base_uri + "/webwxgetcontact");
		client.getQueryMap().put("pass_ticket", meta.pass_ticket);
		client.getQueryMap().put("skey", meta.skey);
		client.getQueryMap().put("r", DateUtil.seconds());
		client.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		client.connect();
		
		JSONObject data = client.getResponseByJsonObject();
		this.validateRet(data, "获取联系人失败");
		JSONArray contactList = new JSONArray();
		JSONArray memberList = data.getJSONArray("MemberList");
		for (int i = 0; i < memberList.size(); i++) {
			JSONObject contact = memberList.getJSONObject(i);
			// 公众号/服务号
			if (contact.getIntValue("VerifyFlag") == 8) {
				continue;
			}
			// 特殊联系人
			if (WxConst.FILTER_USERS.contains(contact.getString("UserName"))) {
				continue;
			}
			// 群聊
			if (contact.getString("UserName").indexOf("@@") != -1) {
				continue;
			}
			// 自己
			if (contact.getString("UserName").equals(meta.user.getString("UserName"))) {
				continue;
			}
			contactList.add(contact);
		}
		meta.contactList = contactList;
		meta.memberList = data.getJSONArray("MemberList");
	}
	
	private HttpClient createHttpClient(String url) {
		return new HttpClient(url, meta.httpClientConfig);
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
		meta.uuid = data.getString("window.QRLogin.uuid");
	}
	
	/**
	 * 显示二维码
	 */
	@SneakyThrows
	public void getQrCode() {
		@Cleanup
		HttpClient c = createHttpClient(WxConst.QRCODE_URL + meta.uuid);
		c.getQueryMap().put("t", "webwx");
		c.getQueryMap().put("_", DateUtil.seconds());
		c.connect();
		
		FileUtils.copyInputStreamToFile(c.getResponseByStream(), meta.file_qrCode);
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
			c.getQueryMap().put("uuid", meta.uuid);
			c.getQueryMap().put("_", DateUtil.seconds());
			c.connect();
			
			JSONObject data = this.parseWxSpec(c.getResponseByString());
			int code = data.getIntValue("window.code");
			if (201 == code) {
				tip = 1;
				log.info("成功扫描,请在手机上点击确认以登录");
			} else if (code == 200) {
				log.info("正在登录...");
				meta.redirect_uri = data.getString("window.redirect_uri") + "&fun=new";
				meta.base_uri = StringUtils.substringBeforeLast(meta.redirect_uri, "/");
				break;
			} else if (code == 408) {
				throw new RuntimeException("登录超时");
			} else {
				log.info("扫描code=" + code);
			}
			TimeUnit.SECONDS.sleep(2);;
		}
	}
	
	/**
	 * 登录
	 */
	public void login() {
		@Cleanup
		HttpClient c = createHttpClient(meta.redirect_uri);
		c.connect();
		String data = c.getResponseByString();
		meta.skey = RegexUtil.matchFirstGroup(data, "<skey>(\\S+)</skey>");
		meta.wxsid = RegexUtil.matchFirstGroup(data, "<wxsid>(\\S+)</wxsid>");
		meta.wxuin = RegexUtil.matchFirstGroup(data, "<wxuin>(\\S+)</wxuin>");
		meta.pass_ticket = RegexUtil.matchFirstGroup(data, "<pass_ticket>(\\S+)</pass_ticket>");

		meta.baseRequest = new JSONObject();
		meta.baseRequest .put("Uin", meta.wxuin);
		meta.baseRequest .put("Sid", meta.wxsid);
		meta.baseRequest .put("Skey", meta.skey);
		meta.baseRequest .put("DeviceID", meta.deviceId);
	}

	/**
	 * 打开状态提醒
	 */
	public void openStatusNotify() {
		@Cleanup
		HttpClient client = createHttpClient(meta.base_uri + "/webwxstatusnotify");
		client.getQueryMap().put("lang", "zh_CN");
		client.getQueryMap().put("pass_ticket", meta.pass_ticket);
		client.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		client.getContentJSONObject().put("Code", 3);
		client.getContentJSONObject().put("FromUserName", meta.user.getString("UserName"));
		client.getContentJSONObject().put("ToUserName", meta.user.getString("UserName"));
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
		HttpClient client = createHttpClient(meta.base_uri + "/webwxinit");
		client.getQueryMap().put("r", DateUtil.seconds());
		client.getQueryMap().put("pass_ticket", meta.pass_ticket);
		client.getQueryMap().put("skey", meta.skey);
		client.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		client.connect();
		
		JSONObject data = client.getResponseByJsonObject();
		this.validateRet(data, "微信初始化失败");
		meta.syncKey = data.getJSONObject("SyncKey");
		meta.user = data.getJSONObject("User");
		this.refreshSyncKeyStr();
	}
	
	private void refreshSyncKeyStr() {
		StringBuffer synckey = new StringBuffer();
		JSONArray list = meta.syncKey.getJSONArray("List");
		for (int i = 0, len = list.size(); i < len; i++) {
			JSONObject item = list.getJSONObject(i);
			synckey.append("|" + item.getIntValue("Key") + "_" + item.getIntValue("Val"));
		}
		meta.synckeyStr = synckey.substring(1);
	}
	
	/**
	 * 选择同步线路
	 */
	public void choiceSyncLine() {
		for(String syncUrl : WxConst.SYNC_HOST){
			try {
				meta.webpush_url = "https://" + syncUrl + "/cgi-bin/mmwebwx-bin/synccheck";
				int[] res = this.syncCheck();
				if(res[0] == 0) {
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
	public int[] syncCheck() {
		@Cleanup
		HttpClient c = createHttpClient(meta.webpush_url);
		c.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		c.getQueryMap().put("r", System.nanoTime());
		c.getQueryMap().put("skey",	meta.skey);
		c.getQueryMap().put("uin", meta.wxuin);
		c.getQueryMap().put("sid", meta.wxsid);
		c.getQueryMap().put("deviceid", meta.deviceId); 
		c.getQueryMap().put("synckey", meta.synckeyStr); 
		c.getQueryMap().put("_", System.currentTimeMillis());
		
		System.out.println(meta.webpush_url);
		System.out.println(c.getQueryMap());
		c.connect();
		////window.synccheck={retcode:"0",selector:"0"}
		JSONObject data = this.parseWxSpec(c.getResponseByString()).getJSONObject("window.synccheck");
		System.out.println(data);
		return new int[] {data.getIntValue("retcode"), data.getIntValue("selector")};
	}

	/**
	 * 处理消息
	 */
	public void handleMsg(JSONObject data) {
		System.out.println("*********************" + data);
		if (data != null) {
			for (JSONObject msg : JsonUtil.toJsonObjects(data.get("AddMsgList"))) {
				int msgType = msg.getIntValue("MsgType");
				String name = getUserRemarkName(msg.getString("FromUserName"));
				String content = msg.getString("Content");

				if (msgType == 51) {
					log.info("成功截获微信初始化消息");
				} else if (msgType == 1) {
					if (WxConst.FILTER_USERS.contains(msg.getString("ToUserName"))) {
						continue;
					} else if (msg.getString("FromUserName").equals(meta.user.getString("UserName"))) {
						continue;
					} else if (msg.getString("ToUserName").indexOf("@@") != -1) {
						String[] peopleContent = content.split(":<br/>");
						log.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
					} else {
						log.info(name + ": " + content);
//						String ans = robot.talk(content);
//						webwxsendmsg(wechatMeta, ans, msg.getString("FromUserName"));
//						log.info("自动回复 " + ans);
					}
				} else if (msgType == 3) {
					String msgId = msg.getString("MsgId");
					@Cleanup
					HttpClient c = createHttpClient(meta.base_uri + "/webwxgetmsgimg");
					c.getQueryMap().put("MsgID", msgId);
					c.getQueryMap().put("skey", meta.skey);
					c.getQueryMap().put("type", "slave");
					c.connect();
					File imagePath = FileUtils.getFile(meta.dir_root, "image", msgId + ".jpg");
					imagePath.getParentFile().mkdirs();
					log.info(imagePath);
//					webwxsendmsg(wechatMeta, "二蛋还不支持图片呢", msg.getString("FromUserName"));
				} else if (msgType == 34) {
//					webwxsendmsg(wechatMeta, "二蛋还不支持语音呢", msg.getString("FromUserName"));
				} else if (msgType == 42) {
					log.info(name + " 给你发送了一张名片:");
					log.info("=========================");
				}
			}
		}
	}
	
	/**
	 * 发送消息
	 */
	private void webwxsendmsg(String content, String to) {
		String clientMsgId = System.nanoTime() + "";
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", meta.user.getString("UserName"));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		@Cleanup
		HttpClient c = createHttpClient(meta.base_uri + "/webwxsendmsg");
		c.getQueryMap().put("lang", "zh_CN");
		c.getQueryMap().put("pass_ticket", meta.pass_ticket);
		c.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		c.getContentJSONObject().put("Msg", Msg);
		c.connect();
		
		log.info("发送消息...");
	}
	
	private String getUserRemarkName(String id) {
		String name = "这个人物名字未知";
		for (JSONObject member : JsonUtil.toJsonObjects(meta.memberList)) {
			if (member.getString("UserName").equals(id)) {
				if (StringUtils.isNotBlank(member.getString("RemarkName"))) {
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}
	
	public JSONObject webwxsync(){
		@Cleanup
		HttpClient c = createHttpClient(meta.base_uri + "/webwxsync");
		c.getQueryMap().put("skey", meta.skey);
		c.getQueryMap().put("sid", meta.wxsid);
		c.getContentJSONObject().put("BaseRequest", meta.baseRequest);
		c.getContentJSONObject().put("SyncKey", meta.syncKey);
		c.getContentJSONObject().put("rr", DateUtil.seconds());
		c.connect();
		
		JSONObject data = c.getResponseByJsonObject();
		this.validateRet(data, "同步syncKey失败");
		meta.syncKey = data.getJSONObject("SyncKey");
		this.refreshSyncKeyStr();
		
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