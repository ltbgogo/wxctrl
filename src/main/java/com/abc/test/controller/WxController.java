package com.abc.test.controller;

import static com.abc.test.repository.RepoFactory.f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.test.domain.WxAccount;
import com.abc.test.domain.WxMsg;
import com.abc.test.manager.UserManager;
import com.abc.test.repository.UserRepository;
import com.abc.test.repository.WxAccountRepository;
import com.abc.test.repository.WxMsgRepository;
import com.abc.test.utility.JsonUtil;
import com.abc.test.utility.ReturnVO;
import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMeta;
import com.abc.test.wx.WxMetaStorage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/wx")
public class WxController {
	
	@Autowired
	private WxAccountRepository wxAccountRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WxMsgRepository wxMsgRepo;
	@Autowired
	private WxApp wxApp;
	
	@RequestMapping("test")
	ModelAndView test() {
		return new ModelAndView("test");
	}
	
	@RequestMapping("preAddClient")
	ModelAndView preAddClient() {
		return new ModelAndView("addClient");
	}
	
    @RequestMapping("addClient")
    ReturnVO addClient() {
		//元数据
    	WxMeta meta = this.wxApp.startOne();
    	UserManager.getUserInfo().setLoginMeta(meta);
		return ReturnVO.SUCCESS;
    }
    
    @RequestMapping("listClients")
    ModelAndView listClients() {
    	return new ModelAndView("listClients", "r", ReturnVO.succeed(UserManager.getUserInfo().getUser().getWxAccounts()));
    }
    
    /**
     * 输出登录扫描码
     */
    @RequestMapping("outputQrcode")
    void outputQrcode(HttpServletResponse response) throws IOException {
    	FileUtils.copyFile(UserManager.getUserInfo().getLoginMeta().getQrCodeImg(), response.getOutputStream());
    }
    
    /**
     * 客户端控制台
     */
    @RequestMapping("clientConsole/{wxAccountId}")
    ModelAndView clientConsole(@PathVariable("wxAccountId") String wxAccountId) {
    	WxAccount account = wxAccountRepo.findOne(wxAccountId);
    	List<String> groupNickNames = wxMsgRepo.findGroupNickNames(account);
    	List<String> contactNickNames = new ArrayList<>();
    	for (Object o : WxMetaStorage.get(account).getContactList()) {
    		contactNickNames.add(((JSONObject) o).getString("NickName"));
    	}
    	return new ModelAndView("clientConsole", "account", account)
    			.addObject("contactNickNames", contactNickNames)
    			.addObject("groupNickNames", groupNickNames);
    }
    
    /**
     * 客户端控制台-获取对话内容
     */
    @RequestMapping("showMsgList")
    ReturnVO clientConsole(String wxAccountId, String type, String contactName, 
    		@PageableDefault(value = 15, sort = {"createTime"}, direction = Direction.DESC) Pageable pageable) {
    	WxAccount account = wxAccountRepo.findOne(wxAccountId);
    	Page<WxMsg> page = null;
    	if ("group".equals(type)) {
    		page = wxMsgRepo.findGroupMsg(account, contactName, pageable);
    	} else {
    		page = wxMsgRepo.findContactMsg(account, contactName, pageable);
    	}
    	List<WxMsg> content = new ArrayList<>(page.getContent());
		Collections.reverse(content);
		return ReturnVO.succeed(new PageImpl<>(content, pageable, page.getTotalElements()));
    }
    
    /**
     * 客户端控制台-获取对话内容
     */
    @RequestMapping("sendMsg")
    ReturnVO sendMsg(String wxAccountId, String type, String nickName, String msg) {
    	WxMeta meta = WxMetaStorage.get(wxAccountId);
    	if (meta == null) {
    		return ReturnVO.fail("账号处于离线状态！");
    	} else {
    		boolean isGrpMsg = "group".equals(type);
    		JSONArray members = isGrpMsg ? meta.getGroupList() : meta.getContactList();
    		for (int i = 0; i < members.size(); i++) {
    			JSONObject member = members.getJSONObject(i);
    			if (nickName.equals(member.getString("NickName"))) {
    				meta.getHttpClient().webwxsendmsg(msg, member.getString("UserName"));
    				//持久化消息到数据库
    				if (isGrpMsg) {
    					meta.getMsg2DB().handleMe2Grp(null, 1, nickName, msg, new Date());
    				} else {
    					meta.getMsg2DB().handleMe2Contact(null, 1, nickName, msg, new Date());
    				}
    			}
    		}
    		return ReturnVO.SUCCESS;
    	}
    }
}






