package com.abc.wxctrl.wx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import com.abc.utility.misc.DateUtil;
import com.abc.utility.misc.JsonUtil;
import com.abc.utility.misc.RegexUtil;
import com.abc.utility.misc.StringUtil;
import com.abc.utility.misc.XmlUtil;

@Log4j
public class WxMsgListener implements Runnable {

	public WebWeixin webWeixin;
	private boolean isStop = false;
	
	public void exit() {
		this.isStop = true;
	}
	
	public WxMsgListener(WebWeixin webWeixin) {
		this.webWeixin = webWeixin;
	}
	
	@SneakyThrows
	public void run() {
		log.info("[*] 进入消息监听模式 ... 成功");
		log.info("[*] 进行同步线路测试 ... ");
		this.testsynccheck();
		int playWeChat = 0;
		int redEnvelope = 0;
		while (!this.isStop) {
			this.webWeixin.lastCheckTs = DateUtil.seconds();
			Map<String, Object> data = this.synccheck();
			log.info(data.toString());
			if (data != null) {
				int retcode = Integer.valueOf(data.get("retcode").toString());
				if (retcode == 1100) {
					log.info("[*] 你在手机上登出了微信，再见");
				} else if (retcode == 1101) {
					log.info("[*] 你在其他地方登录了 WEB 版微信，再见");
				} else if (retcode == 0) {
					int selector = Integer.valueOf(data.get("selector").toString());
					//有消息到来
					if (selector == 2) {
						Map<String, Object> r = this.webwxsync();
						if (r != null) {
							 this.handleMsg(r);
						} else if (selector == 6) {
							//# TODO
		                    redEnvelope += 1;
		                    log.info(String.format("[*] 收到疑似红包消息 %s 次", redEnvelope));
						} else if (selector == 7) {
							playWeChat += 1;
							log.info(String.format("[*] 你在手机上玩微信被我发现了 %d 次", playWeChat));
				            r = this.webwxsync();
						} else if (selector == 0) {
							TimeUnit.SECONDS.sleep(1);
						}
					}
				}
				if (DateUtil.seconds() - this.webWeixin.lastCheckTs <= 20) {
					TimeUnit.SECONDS.sleep(DateUtil.seconds() - this.webWeixin.lastCheckTs);
				}
			}
		}
	}
	
	private String getGroupName(String id) {
		String name = "未知群";
		for (Map<String, Object> member : this.webWeixin.GroupList) {
			if (member.get("UserName").equals(id)) {
				name = (String) member.get("NickName");
			}
		}
		if ("未知群".equals(name)) {
			//现有群里面查不到
			List<Map<String, Object>> GroupList = this.getNameById(id);
			if (GroupList != null) {
				for (Map<String, Object> group : GroupList) {
					this.webWeixin.GroupList.add(group);
					if (id.equals(group.get("UserName"))) {
						name = (String) group.get("NickName");
						List<Map<String, Object>> MemberList = (List<Map<String, Object>>) group.get("MemberList");
						for (Map<String, Object> member : MemberList) {
							this.webWeixin.GroupMemeberList.add(member);
						}
					}
				}
			}
		}
		return name;
	}

	private List<Map<String, Object>> getNameById(String id) {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.base_uri + "/webwxbatchgetcontact", this.webWeixin.httpClientConfig);
        client.getRequestHeaderMap().put("Content-Type", "application/json;charset=UTF-8");
        
        client.getQueryMap().put("pass_ticket", this.webWeixin.accessToken.getPass_ticket());
        client.getQueryMap().put("type", "ex");
        client.getQueryMap().put("r", DateUtil.seconds());
        
        client.getContentJsonOfMap().put("BaseRequest", this.webWeixin.accessToken.toBaseRequest());
        client.getContentJsonOfMap().put("Count", this.webWeixin.GroupList.size());
        client.getContentJsonOfMap().put("List", ArrayUtils.toMap(new Object[][] {{"UserName", id}, {"EncryChatRoomId", ""}}));
        
        client.connect();
        
        Map<String, Object> data = client.getContentJsonOfMap();
        return (List<Map<String, Object>>) data.get("ContactList");
	}
	
	private String getUserRemarkName(String id) {
		String name = "未知群";
		if (id.startsWith("@@")) {
			name = "";
		} else {
			name = "陌生人";
		}
		//自己
		if (id.equals(this.webWeixin.user.get("UserName"))) {
			return (String) this.webWeixin.user.get("NickName"); 
		}
		//群
		if (id.startsWith("@@")) {
			name = this.getGroupName(id);
		} else {
			 //特殊账号 
			for (Map<String, Object> member : this.webWeixin.SpecialUsersList) {
				if (id.equals(member.get("UserName"))) {
					if (StringUtils.isBlank(member.get("RemarkName").toString())) {
						name = (String) member.get("NickName");
					} else {
						name = (String) member.get("RemarkName");
					}
				}
			}
			//公众号或服务号
			for (Map<String, Object> member : this.webWeixin.PublicUsersList) {
				if (id.equals(member.get("UserName"))) {
					if (StringUtils.isBlank(member.get("RemarkName").toString())) {
						name = (String) member.get("NickName");
					} else {
						name = (String) member.get("RemarkName");
					}
				}
			}
			//直接联系人
			for (Map<String, Object> member : this.webWeixin.ContactList) {
				if (id.equals(member.get("UserName"))) {
					if (StringUtils.isBlank(member.get("RemarkName").toString())) {
						name = (String) member.get("NickName");
					} else {
						name = (String) member.get("RemarkName");
					}
				}
			}
			//群友
			for (Map<String, Object> member : this.webWeixin.GroupMemeberList) {
				if (id.equals(member.get("UserName"))) {
					if (StringUtils.isBlank(member.get("DisplayName").toString())) {
						name = (String) member.get("NickName");
					} else {
						name = (String) member.get("DisplayName");
					}
				}
			}
		}
		return name;
	}
	
	private String _searchContent(String key, String content, String format) {
		String r = "未知";
		if ("attr".equals(format)) {
			List<List<String>> groups = RegexUtil.matchGroups(content, key + "\\s?=\\s?\"([^\"<]+)\"");
			if (!groups.isEmpty()) {
				r = groups.get(0).get(0);
			}
		} else if ("xml".equals(format)) {
			List<List<String>> groups = RegexUtil.matchGroups(String.format("<%$1s>([^<]+)</%$1s>", key), content);
			if (groups.isEmpty()) {
				groups = RegexUtil.matchGroups(String.format("<%$1s><\\!\\[CDATA\\[(.*?)\\]\\]></%$1s>", key), content);
			} 
			r = groups.get(0).get(0);
		}
		return r;
	}
	
	@SneakyThrows
	private void _showMsg(Map<String, Object> msg) {
		String srcName = null;
		String dstName = null;
		String groupName = null;
		String content = null;
		Map<String, Object> rawMsg = (Map<String, Object>) msg.get("raw_msg");
		if (rawMsg != null) {
			srcName = this.getUserRemarkName(rawMsg.get("FromUserName").toString());
			dstName = this.getUserRemarkName(rawMsg.get("ToUserName").toString());
			content = StringUtil.escapeHtml(rawMsg.get("Content").toString());
			String message_id = (String) rawMsg.get("MsgId");
			if (content.contains("http://weixin.qq.com/cgi-bin/redirectforward?args=")) {
				//地理位置消息
				@Cleanup
				WxHttpClient client = new WxHttpClient(content, this.webWeixin.httpClientConfig);
				client.connect();
				byte[] data = client.getResponseByBytes();
				if (data.length == 0) {
					return;
				}
				String pos = this._searchContent("title", new String(data, "GBK"), "xml");
				client.close();
				
				@Cleanup
				WxHttpClient client1 = new WxHttpClient(content, this.webWeixin.httpClientConfig);
				client1.connect();
				String tmp = client1.getResponseByString();
//				Document tree = XmlUtil.XU.getDocument(tmp);
//				String url = XmlUtil.XU.getString("//html/body/div/img/@src", tree);
//				for item in urlparse(url).query.split('&'):
//                    if item.split('=')[0] == 'center':
//                        loc = item.split('=')[-1:]
//                content = '%s 发送了一个 位置消息 - 我在 [%s](%s) @ %s]' % (
//                    srcName, pos, url, loc)
			} //文件传输助手
			else if ("filehelper".equals(rawMsg.get("ToUserName"))) {
				dstName = "文件传输助手";
			} //接收到来自群的消息
			else if (rawMsg.get("FromUserName").toString().startsWith("@@")) {
				if (content.contains(":<br/>")) {
					String people = StringUtils.substringBefore(content, ":<br/>");
					content = StringUtils.substringAfter(content, ":<br/>");
					groupName = srcName;
					srcName = this.getUserRemarkName(people);
					dstName = "GROUP";
				} else {
					groupName = srcName;
		            srcName = "SYSTEM";
				}
			} //自己发给群的消息
			else if (rawMsg.get("ToUserName").toString().endsWith("@@")) {
				groupName = dstName;
		        dstName = "GROUP";
			} //收到了红包
			else if ("收到红包，请在手机上查看".equals(content)) {
				msg.put("message", content);
			} //指定了消息内容
			else if (msg.containsKey("message")) {
				content = msg.get("message").toString();
			}
			if (StringUtils.isNotBlank(groupName)) {
				log.info(String.format("%s |%s| %s -> %s: %s", message_id, groupName.trim(), srcName.trim(), dstName.trim(), content.replace("<br/>", "\n")));
				log.info(String.format("%s |%s| %s -> %s: %s", message_id, groupName.trim(), srcName.trim(), dstName.trim(), content.replace("<br/>", "\n")));
			} else {
				log.info(String.format("%s %s -> %s: %s", message_id, srcName.trim(), dstName.trim(), content.replace("<br/>", "\n")));
			    log.info(String.format("%s %s -> %s: %s", message_id, srcName.trim(), dstName.trim(), content.replace("<br/>", "\n")));
			}
		}
	}
	
	private String _xiaodoubi(String word) {
//	        url = 'http://www.xiaodoubi.com/bot/chat.php'
//	        try:
//	            r = requests.post(url, data={'chat': word})
//	            return r.content
//	        except:
	            return "让我一个人静静 T_T...";
	}
	
	private File webwxgetmsgimg(String msgid) {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.base_uri + "/webwxgetmsgimg", this.webWeixin.httpClientConfig);
		client.getQueryMap().put("MsgID", msgid);
		client.getQueryMap().put("skey", this.webWeixin.accessToken.getSkey());
		byte[] data = client.getResponseByBytes();
		return this.webWeixin.saveFile("img_" + msgid + ".jpg", data, "webwxgetmsgimg");
	}
	
	@SneakyThrows
	private void _safe_open(String path) {
//		if (this.webWeixin.config.isAutoOpen()) {
//			if (SystemUtils.IS_OS_LINUX) {
//				Runtime.getRuntime().exec(String.format("xdg-open %s &", path));
//			} else {
//				Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + path.toString()});
//			}
//		}
	}

	private File webwxgetvoice(String msgid) {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.base_uri + "/webwxgetvoice", this.webWeixin.httpClientConfig);
		client.getQueryMap().put("msgid", msgid);
		client.getQueryMap().put("skey", this.webWeixin.accessToken.getSkey());
		byte[] data = client.getResponseByBytes();
		if (data.length == 0) {
			return null;
		}
		return this.webWeixin.saveFile("voice_" + msgid + ".mp3", data, "webwxgetvoice");
	}
	
	//Not work now for weixin haven't support this API
	private File webwxgetvideo(String msgid) {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.base_uri + "/webwxgetvideo", this.webWeixin.httpClientConfig);
		client.getQueryMap().put("msgid", msgid);
		client.getQueryMap().put("skey", this.webWeixin.accessToken.getSkey());
		byte[] data = client.getResponseByBytes();
		if (data.length == 0) {
			return null;
		}
		return this.webWeixin.saveFile("video_" + msgid + ".mp4", data, "webwxgetvideo");
	}
	
	private void handleMsg(Map<String, Object> r) {
		for (Map<String, Object> msg : (List<Map>) r.get("AddMsgList")) {
			log.info("[*] 你有新的消息，请注意查收");
			int msgType = (Integer) msg.get("MsgType");
			String name = this.getUserRemarkName(msg.get("FromUserName").toString());
			String content = StringUtil.escapeHtml(msg.get("Content").toString());
			String msgId = msg.get("MsgId").toString();
			if (msgType == 1) {
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {{"raw_msg", msg}});
				this._showMsg(raw_msg);
//				#自己加的代码-------------------------------------------#
//                #if self.autoReplyRevokeMode:
//                #    store
//                #自己加的代码-------------------------------------------#
//				if (this.webWeixin.config.isAutoReplyMode()) {
//					String ans = this._xiaodoubi(content) + "\n[微信机器人自动回复]";
//					log.info("自动回复: " + ans);
//				} else {
//					log.info("自动回复失败");
//				}
			} else if (msgType == 3) {
				File image = this.webwxgetmsgimg(msgId);
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 发送了一张图片: %s", name, image)}
				});
				this._showMsg(raw_msg);
				this._safe_open(image.toString());
			} else if (msgType == 34) {
				File voice = this.webwxgetvoice(msgId);
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 发了一段语音: %s", name, voice)}
				});
				this._showMsg(raw_msg);
				this._safe_open(voice.toString());
			} else if (msgType == 42) {
				Map<String, Object> info = (Map) msg.get("RecommendInfo");
				log.info(String.format("%s 发送了一张名片:", name));
				log.info(String.format("========================="));
				log.info(String.format("= 昵称: %s", info.get("NickName")));
				log.info(String.format("= 微信号: %s", info.get("Alias")));
				log.info(String.format("= 地区: %s %s", info.get("Province"), info.get("City")));
				log.info(String.format("= 性别: %s", StringUtils.defaultIfBlank(info.get("Sex").toString(), "未知")));
				log.info(String.format("========================="));
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 发送了一张名片: %s", name.trim(), JsonUtil.toJson(info))}
				});
                this._showMsg(raw_msg);
			} else if (msgType == 47) {
				String url = this._searchContent("cdnurl", content, "attr");
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 发了一个动画表情，点击下面链接查看: %s", name.trim(), url)}
				});
                this._showMsg(raw_msg);
                this._safe_open(url);
			} else if (msgType == 49) {
				Map<Integer, Object> appMsgType = (Map) ArrayUtils.toMap(new Object[][] {
						{5, "链接"},
						{3, "音乐"},
						{7, "微博"}
				});
				log.info(String.format("%s 分享了一个%s:", name, appMsgType.get(msg.get("AppMsgType"))));
				log.info("=========================");
				log.info(String.format("= 标题: %s", msg.get("FileName")));
				log.info(String.format("= 描述: %s", this._searchContent("des", content, "xml")));
				log.info(String.format("= 链接: %s", msg.get("Url")));
				log.info(String.format("= 来自: %s", this._searchContent("appname", content, "xml")));
				log.info(String.format("========================="));
				Map<String, Object> card = (Map) ArrayUtils.toMap(new Object[][] {
						{"title", msg.get("FileName")},
		                {"description", this._searchContent("des", content, "xml")},
		                {"url", msg.get("Url")},
		                {"appname", this._searchContent("appname", content, "xml")}
				});
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 分享了一个%s: %s", name, appMsgType.get(msg.get("AppMsgType")), JsonUtil.toJson(card))}
				});
				this._showMsg(raw_msg);
			} else if (msgType == 51) {
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", "[*] 成功获取联系人信息"}
				});
				this._showMsg(raw_msg);
			} else if (msgType == 62) {
				File video = this.webwxgetvideo(msgId);
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 发了一段小视频: %s", name, video)}
				});
				this._showMsg(raw_msg);
				this._safe_open(video.toString());
			} else if (msgType == 10002) {
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("%s 撤回了一条消息", name)}
				});
				this._showMsg(raw_msg);
			} else {
				Map<String, Object> raw_msg = (Map) ArrayUtils.toMap(new Object[][] {
						{"raw_msg", msg},
						{"message", String.format("[*] 该消息类型为: %d，可能是表情，图片, 链接或红包", msg.get("MsgType"))}
				});
			}
		}
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private Map<String, Object> webwxsync() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.base_uri + "/webwxsync", this.webWeixin.httpClientConfig);
        client.getRequestHeaderMap().put("Content-Type", "application/json;charset=UTF-8");
        
        client.getQueryMap().put("sid", this.webWeixin.accessToken.getWxsid());
        client.getQueryMap().put("skey", this.webWeixin.accessToken.getSkey());
        client.getQueryMap().put("pass_ticket", this.webWeixin.accessToken.getPass_ticket());
        
        client.getContentJsonOfMap().put("BaseRequest", this.webWeixin.accessToken.toBaseRequest());
        client.getContentJsonOfMap().put("SyncKey", this.webWeixin.syncKeyStr);
        client.getContentJsonOfMap().put("rr", ~DateUtil.seconds());
        
        client.connect();

        Map<String, Object> data = client.getResponseByJson(Map.class);
        this.webWeixin.saveFile("msg_" + DateUtil.seconds() + "a.txt", 
    		   JsonUtil.toPrettyJson(data).getBytes("UTF-8"), "msg");
       
        log.info("webwxsync*****" + JsonUtil.toPrettyJson(data));
        if ((Integer) ((Map) data.get("BaseResponse")).get("Ret") == 0) {
        	this.webWeixin.syncKey = (Map) data.get("SyncKey");
        	this.webWeixin.refreshSyncKeyStr();
        }
        return data;
	}
	
	private void testsynccheck() {
		String[] syncHosts = {"webpush.wx.qq.com",
				"wx2.qq.com",
                "webpush.wx2.qq.com",
                "wx8.qq.com",
                "webpush.wx8.qq.com",
                "web2.wechat.com",
                "webpush.web2.wechat.com",
                "wechat.com",
                "webpush.web.wechat.com",
                "webpush.weixin.qq.com",
                "webpush.wechat.com",
                "webpush1.wechat.com",
                "webpush2.wechat.com",
                "webpush.wx.qq.com",
                "webpush2.wx.qq.com"};
		for (String host : syncHosts) {
			webWeixin.syncHost = host;
			Map<String, Object> data = this.synccheck();
			if ("0".equals(data.get("retcode"))) {
				return;
			}
		}
	}
	
	private Map<String, Object> synccheck() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.webWeixin.httpClientConfig);
        client.setUrl("https://" + this.webWeixin.syncHost + "/cgi-bin/mmwebwx-bin/synccheck");
        
        client.getQueryMap().put("r", DateUtil.seconds());
        client.getQueryMap().put("sid", this.webWeixin.accessToken.getWxsid());
        client.getQueryMap().put("uin", this.webWeixin.accessToken.getWxuin());
        client.getQueryMap().put("skey", this.webWeixin.accessToken.getSkey());
        client.getQueryMap().put("deviceid", this.webWeixin.deviceId);
        client.getQueryMap().put("synckey", this.webWeixin.syncKeyStr);
        client.getQueryMap().put("_", DateUtil.seconds());

        client.connect();
        //window.synccheck={retcode:"0",selector:"0"}
        if (client.getResponseCode() == 405) {
        	return null;
        }
        Map<String, Object> data = client.getResponseByWxMap();
        return (Map) data.get("window.synccheck");
	}
}
