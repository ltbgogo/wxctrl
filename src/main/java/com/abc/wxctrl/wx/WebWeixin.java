package com.abc.wxctrl.wx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.abc.utility.io.IOUtil;
import com.abc.utility.io.NetUtil;
import com.abc.utility.misc.DateUtil;
import com.abc.utility.misc.JsonUtil;
import com.abc.utility.misc.NumUtil;
import com.abc.utility.misc.RegexUtil;
import com.abc.utility.misc.XmlUtil;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

/**
 * https://wx.qq.com/
 */
@Log4j
public class WebWeixin {
	
	public WebWeixin() {
		httpClientConfig.getRequestHeaderMap().put("Host", "wx.qq.com");
		httpClientConfig.getRequestHeaderMap().put("Referer", "https://wx.qq.com/?&lang=zh_CN");
		httpClientConfig.setCookieStore(new WxCookieStore());
//		httpClientConfig.setProxy(new Proxy(Type.HTTP, new InetSocketAddress("proxy3.bj.petrochina", 8080)));
		httpClientConfig.setEnableSNIExtension(false);
	}
	
	protected WxHttpClientConfig httpClientConfig = new WxHttpClientConfig();
	//扫码登录时要传递的参数
	protected String qrLoginUUID;
	//扫码登录成功后，用于获取accessToken的链接
	protected String redirect_uri;
	//登录成功后，数据请求的基本地址
	protected String base_uri;
	protected WxAccessToken accessToken;
	protected String deviceId = "e" + System.nanoTime();
	// synckey for synccheck
	protected String syncKeyStr;
	protected Map<String, Object> syncKey;
	protected Map<String, Object> user;
	protected int MemberCount = 0;
	protected List<Map<String, Object>> MemberList;
	protected List<Map<String, Object>> ContactList = new ArrayList<Map<String, Object>>();  // 好友
	protected List<Map<String, Object>> GroupList = new ArrayList<Map<String, Object>>();  // 群
	protected List<Map<String, Object>> GroupMemeberList = new ArrayList<Map<String, Object>>();  // 群友
	protected List<Map<String, Object>> PublicUsersList = new ArrayList<Map<String, Object>>();  // 公众号／服务号
	protected List<Map<String, Object>> SpecialUsersList = new ArrayList<Map<String, Object>>();  // 特殊账号
	protected String syncHost = "";
	protected File saveFolder = new File("E:\\test\\wx"); //存储文件的目录
	protected String appid = "wx782c26e4c19acffb";
	protected String lang = "zh_CN";
	protected long lastCheckTs = System.currentTimeMillis() / 1000;
	
	protected List<String> SpecialUsers = Arrays.asList("newsapp,fmessage,filehelper,weibo,qqmail,fmessage,tmessage,qmessage,qqsync,floatbottle,lbsapp,shakeapp,medianote,qqfriend,readerapp,blogapp,facebookapp,masssendapp,meishiapp,feedsapp,voip,blogappweixin,weixin,brandsessionholder,weixinreminder,wxid_novlwrv3lqwv11,gh_22b87fa7cb3c,officialaccounts,notification_messages,wxid_novlwrv3lqwv11,gh_22b87fa7cb3c,wxitil,userexperience_alarm,notification_messages".split(","));
	protected int TimeOut = 20;  // 同步最短时间间隔（单位：秒）
	protected int media_count = -1;
//	self.saveSubFolders = {'webwxgeticon': 'icons', 
//			'webwxgetheadimg': 'headimgs', 
//			'webwxgetmsgimg': 'msgimgs',
//            'webwxgetvideo': 'videos', 
//            'webwxgetvoice': 'voices', 
//            '_showQRCodeImg': 'qrcodes'}
	
	public void start() {
		log.info("[*] 微信网页版 ... 开动");
		while (true) {
			log.info("[*] 正在获取 uuid ...");
			this.requestUUID();
			log.info("[*] 正在获取二维码 ...");
			this.showQRCode();
			log.info("[*] 请使用微信扫描二维码以登录 ...");
			this.waitForLogin();
            break;
		}
		log.info("[*] 正在登录 ...");
		this.login();
		log.info("[*] 微信正在初始化 ...");
		this.webwxinit();
		log.info("[*] 开启状态通知 ...");
		this.webwxstatusnotify();
		log.info("[*] 正在获取联系人 ...");
		this.webwxgetcontact();
		log.info(String.format("[*] 应有 %s个联系人，读取到联系人 %d个", 
				this.MemberCount, 
				this.MemberList.size()));
        log.info(String.format("[*] 共有 %d 个群 | %d 个直接联系人 | %d 个特殊账号 ｜ %d 公众号或服务号",
        		this.GroupList.size(),
        		this.ContactList.size(),
        		this.SpecialUsersList.size(),
        		this.PublicUsersList.size()));
        log.info("[*] 获取群 ...");
        this.webwxbatchgetcontact();
        log.info("[*] 微信网页版 ... 开动");
        log.info("[*] 启动消息监听...");
        WxMsgListener listener = new WxMsgListener(this);
        new Thread(listener).start();
//        while (true) {
//        	String text = IOUtil.readLineFromConsole();
//        	if ("quit".equals(text)) {
//        		log.info("[*] 退出微信");
//        		listener.exit();
//        	} else if (text.startsWith("->")) {
//        		String name = StringUtils.substringBefore(text.substring(2), ":");
//        		String word = StringUtils.substringAfter(text.substring(2), ":");
//        		
//        	}
//        }
//        while True:
//            text = input('')
//            if text == 'quit':
//                listenProcess.terminate()
//                print('')
//                logging.debug('[*] 退出微信')
//                exit()
//            elif text[:2] == '->':
//                [name, word] = text[2:].split(':')
//                if name == 'all':
//                    self.sendMsgToAll(word)
//                else:
//                    self.sendMsg(name, word)
//            elif text[:3] == 'm->':
//                [name, file] = text[3:].split(':')
//                self.sendMsg(name, file, True)
//            elif text[:3] == 'f->':
//                print('发送文件')
//                logging.debug('发送文件')
//            elif text[:3] == 'i->':
//                print('发送图片')
//                [name, file_name] = text[3:].split(':')
//                self.sendImg(name, file_name)
//                logging.debug('发送图片')
//            elif text[:3] == 'e->':
//                print('发送表情')
//                [name, file_name] = text[3:].split(':')
//                self.sendEmotion(name, file_name)
//                logging.debug('发送表情')
	}
	
	private void sendMsgToAll(String word) {
		for (Map<String, Object> contact : this.ContactList) {
			String name = (String) contact.get("RemarkName");
			if (StringUtils.isBlank(name)) {
				name = (String) contact.get("NickName");
			}
			String id = (String) contact.get("UserName");
			log.info("");
		}
	}
	
	/**
	 * 获取群
	 */
	private void webwxbatchgetcontact() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.base_uri + "/webwxbatchgetcontact", httpClientConfig);
        client.getRequestHeaderMap().put("Content-Type", "application/json;charset=UTF-8");
        
        client.getQueryMap().put("pass_ticket", this.accessToken.getPass_ticket());
        client.getQueryMap().put("type", "ex");
        client.getQueryMap().put("r", DateUtil.seconds());
        
        client.getContentJsonOfMap().put("BaseRequest", this.accessToken.toBaseRequest());
        client.getContentJsonOfMap().put("Count", this.GroupList.size());
        List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
        for (Map<String, Object> item : this.GroupList) {
        	list.add(ArrayUtils.toMap(new Object[][] {{"UserName", item.get("UserName")}, {"EncryChatRoomId", ""}}));
        }
        client.getContentJsonOfMap().put("List", list);
        client.connect();
        String s = client.getResponseByString();
        test("webwxbatchgetcontact", s);
	}

//	        # blabla ...
//	        ContactList = dic['ContactList']
//	        ContactCount = dic['Count']
//	        self.GroupList = ContactList
//	        for i in range(len(ContactList) - 1, -1, -1):
//	            Contact = ContactList[i]
//	            MemberList = Contact['MemberList']
//	            for member in MemberList:
//	                self.GroupMemeberList.append(member)
//	        return True
	
	/**
	 * 开启状态通知
	 */
	private void webwxstatusnotify() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.base_uri + "/webwxstatusnotify", this.httpClientConfig);

        client.getRequestHeaderMap().put("Content-Type", "application/json;charset=UTF-8");
        client.getQueryMap().put("pass_ticket", this.accessToken.getPass_ticket());
        client.getQueryMap().put("lang", "zh_CN");
        client.getContentJsonOfMap().put("BaseRequest", this.accessToken.toBaseRequest());
        client.getContentJsonOfMap().put("Code", 3);
        client.getContentJsonOfMap().put("FromUserName", this.user.get("UserName"));
        client.getContentJsonOfMap().put("ToUserName", this.user.get("UserName"));
       	client.getContentJsonOfMap().put("ClientMsgId", DateUtil.seconds());
        client.connect();
	}

	/**
	 * 微信初始化
	 */
	@SneakyThrows
	private void webwxinit() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.base_uri + "/webwxinit", httpClientConfig);
        client.getRequestHeaderMap().put("Content-Type", "application/json;charset=UTF-8");
        
        client.getQueryMap().put("pass_ticket", this.accessToken.getPass_ticket());
        client.getQueryMap().put("lang", "zh_CN");
        client.getQueryMap().put("_", DateUtil.seconds());
        
        client.getContentJsonOfMap().put("BaseRequest", this.accessToken.toBaseRequest());
        client.connect();
        Map<String, Object> data = client.getResponseByJson(Map.class);
        
        syncKey = (Map) data.get("SyncKey");
        this.refreshSyncKeyStr();
        this.user = (Map<String, Object>) data.get("User");
	}
	
	protected void refreshSyncKeyStr() {
        List<String> syncKeyStrList = new ArrayList<String>();
        for (Map<String, Object> item : (List<Map<String, Object>>) this.syncKey.get("List")) {
        	syncKeyStrList.add(item.get("Key") + "_" + item.get("Val"));
        }
        this.syncKeyStr = StringUtils.join(syncKeyStrList, "|");
	}
	
	/**
	 * 获取联系人
	 */
	private void webwxgetcontact() {
		@Cleanup
		WxHttpClient client = new WxHttpClient(this.base_uri + "/webwxgetcontact", httpClientConfig);
		
        client.getQueryMap().put("pass_ticket", this.accessToken.getPass_ticket());
        client.getQueryMap().put("lang", "zh_CN");
        client.getQueryMap().put("skey", this.accessToken.getSkey());
        client.getQueryMap().put("_", DateUtil.seconds());
        client.getQueryMap().put("seq", 0);
        client.connect();
        
        Map<String, Object> data = client.getResponseByJson(Map.class);
        this.MemberCount = (Integer) data.get("MemberCount");
        this.MemberList = (List) data.get("MemberList");
        for (Map<String, Object> member : this.MemberList) {
        	//公众号/服务号
        	if ((((Integer) member.get("VerifyFlag")) & 8) != 0) {
        		this.PublicUsersList.add(member);
        	} //特殊账号
        	else if (this.SpecialUsers.contains(member.get("UserName"))) {
        		this.SpecialUsersList.add(member);
        	} //群聊
        	else if (member.get("UserName").toString().contains("@@")) {
        		this.GroupList.add(member);
        	} //自己
        	else if (member.get("UserName").equals(this.user.get("UserName"))) {
        		
        	} //好友 
        	else {
        		this.ContactList.add(member);
        	}
        }
	}
	
	
//	def webwxgetcontact(self):
//        SpecialUsers = self.SpecialUsers
//        url = self.base_uri + '/webwxgetcontact?pass_ticket=%s&skey=%s&r=%s' % (
//            self.pass_ticket, self.skey, int(time.time()))
//        dic = self._post(url, {})
//        if dic == '':
//            return False
//        self.MemberCount = dic['MemberCount']
//        self.MemberList = dic['MemberList']
//        ContactList = self.MemberList[:]
//        GroupList = self.GroupList[:]
//        PublicUsersList = self.PublicUsersList[:]
//        SpecialUsersList = self.SpecialUsersList[:]
//        for i in range(len(ContactList) - 1, -1, -1):
//            Contact = ContactList[i]
//            if Contact['VerifyFlag'] & 8 != 0:  # 公众号/服务号
//                ContactList.remove(Contact)
//                self.PublicUsersList.append(Contact)
//            elif Contact['UserName'] in SpecialUsers:  # 特殊账号
//                ContactList.remove(Contact)
//                self.SpecialUsersList.append(Contact)
//            elif '@@' in Contact['UserName']:  # 群聊
//                ContactList.remove(Contact)
//                self.GroupList.append(Contact)
//            elif Contact['UserName'] == self.User['UserName']:  # 自己
//                ContactList.remove(Contact)
//        self.ContactList = ContactList
//        return True
	
	/**
	 * 等待手机端确认登录
	 */
	@SneakyThrows
	public void waitForLogin() {
		while (true) {
			TimeUnit.SECONDS.sleep(3);
			@Cleanup
			WxHttpClient client = new WxHttpClient("https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login", httpClientConfig);

	        client.getQueryMap().put("_", DateUtil.seconds());
	        client.getQueryMap().put("tip", 0);
	        client.getQueryMap().put("uuid", this.qrLoginUUID);
	        client.connect();
	        
	        Map<String, Object> data = client.getResponseByWxMap();
	        //201是扫描完成，返回用户头像；200是确认登录返回跳转地址
	        if ((Integer) data.get("window.code") == 200) {
	        	this.redirect_uri = (String) data.get("window.redirect_uri");
	        	this.base_uri = StringUtils.substringBeforeLast(this.redirect_uri, "/");
	        	break;
	        }
		}
	}
	
	/**
	 * 生成二维码
	 */
	@SneakyThrows
	private void showQRCode() {
		@Cleanup
		WxHttpClient client = new WxHttpClient("https://login.weixin.qq.com/qrcode/" + this.qrLoginUUID, httpClientConfig);
		
        client.getQueryMap().put("_", DateUtil.seconds());
        client.connect();
        byte[] data = client.getResponseByBytes();
		File qrCodePath = this.saveFile("qrcode.jpg", data, "qrcodes");
		if (SystemUtils.IS_OS_WINDOWS) {
			Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + qrCodePath.toString()});
		}
	}

	/**
	 * 获取回话id
	 * window.QRLogin.code = 200; window.QRLogin.uuid = "QebhTea-mw==";       
	 */
	private void requestUUID() {
        @Cleanup
        WxHttpClient client = new WxHttpClient("https://login.weixin.qq.com/jslogin", httpClientConfig);

        client.getQueryMap().put("appid", this.appid);
        client.getQueryMap().put("fun", "new");
        client.getQueryMap().put("lang", this.lang);
        client.getQueryMap().put("_", DateUtil.seconds());
		client.connect();
		Map<String, Object> data = client.getResponseByWxMap();
        this.qrLoginUUID = (String) data.get("window.QRLogin.uuid");
	}
	
	/**
	 * 保存文件
	 */
	@SneakyThrows
	protected File saveFile(String fileName, byte[] data, String fileCategory) {
		File file = FileUtils.getFile(this.saveFolder, fileCategory, fileName);
		file.getParentFile().mkdirs();
		FileUtils.copyInputStreamToFile(new ByteArrayInputStream(data), file);
		return file;
	}
	
	/**
	 * 登录
	 */
	@SneakyThrows
	private void login() {
        @Cleanup
        WxHttpClient client = new WxHttpClient(this.redirect_uri + "&fun=new&version=v2", httpClientConfig);
        client.connect();
		String data = client.getResponseByString();
		this.accessToken = new WxAccessToken().setData(data, this.deviceId);
	}

	@SneakyThrows
	public void test(String name, String data) {
		FileUtils.writeStringToFile(new File("d://test//" + name + ".txt"), data, "UTF-8");
	}

	public static void main(String[] args) {
		WebWeixin webwx = new WebWeixin();
		webwx.start();
	}
}










