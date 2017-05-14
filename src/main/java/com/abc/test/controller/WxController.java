package com.abc.test.controller;

import static com.abc.test.repository.RepoFactory.f;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    	FileUtils.copyFile(UserManager.getUserInfo().getLoginMeta().getFile_qrCode(), response.getOutputStream());
    }
    
    /**
     * 客户端控制台
     */
    @RequestMapping("clientConsole/{wxAccountId}")
    ModelAndView clientConsole(@PathVariable("wxAccountId") String wxAccountId) {
    	WxAccount account = wxAccountRepo.findOne(wxAccountId);
    	List<String> groupNames = wxMsgRepo.findGroupNames(account);
    	Set<String> individuals = new HashSet<>();
    	individuals.addAll(f.getWxMsgRepo().findToUserName(account));
    	individuals.addAll(f.getWxMsgRepo().findFromUserName(account));
    	return new ModelAndView("clientConsole", "account", account)
    			.addObject("individuals", individuals)
    			.addObject("groupNames", groupNames);
    }
    
    /**
     * 客户端控制台
     */
    @RequestMapping("showMsgList")
    ReturnVO clientConsole(String wxAccountId, String type, String contactName, 
    		@PageableDefault(value = 15, sort = {"createTime"}, direction = Direction.DESC) Pageable pageable) {
    	WxAccount account = wxAccountRepo.findOne(wxAccountId);
    	if ("group".equals(type)) {
    		Page<WxMsg> page = wxMsgRepo.findGroupMsg(account, contactName, pageable);
    		return ReturnVO.succeed(page);
    	} else {
    		Page<WxMsg> page = wxMsgRepo.findContactMsg(account, contactName, pageable);
    		return ReturnVO.succeed(page);
    	}
    }
}






