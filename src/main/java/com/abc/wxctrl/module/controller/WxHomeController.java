package com.abc.wxctrl.module.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.wxctrl.manager.UserManager;
import com.abc.wxctrl.utility.ResultVO;
import com.abc.wxctrl.wx.WxApp;
import com.abc.wxctrl.wx.WxMeta;
import com.abc.wxctrl.wx.WxMeta.WxMetaStatus;

@RestController
@RequestMapping("/wx/home")
public class WxHomeController {
	
	@Autowired
	private WxApp wxApp;
	
    @RequestMapping("listAccount")
    ModelAndView listAccount() {
    	return new ModelAndView("/wx/home/account_list", "r", ResultVO.succeed(UserManager.getCurrent().getWxAccounts()));
    }
	
    @RequestMapping("addAccount")
    ResultVO addAccount() {
    	WxMeta meta = UserManager.getData(UserManager.NAME_LOGIN_META); 
    	if (meta == null || meta.getMetaStatus() != WxMetaStatus.waitForLogin) {
        	UserManager.setData(UserManager.NAME_LOGIN_META, this.wxApp.startOne());
    	}
		return ResultVO.SUCCESS;
    }
    
    /**
     * 输出登录扫描码
     */
    @RequestMapping("outputQrcode")
    void outputQrcode(HttpServletResponse response) throws IOException {
    	FileUtils.copyFile(UserManager.getData(UserManager.NAME_LOGIN_META, WxMeta.class).getQrCodeImg(), response.getOutputStream());
    }
    
    /**
     * 查看登录状态
     */
    @RequestMapping("checkLoginStatus")
    ResultVO checkLoginStatus() {
    	WxMeta meta = UserManager.getData(UserManager.NAME_LOGIN_META, WxMeta.class);
    	return ResultVO.succeed(meta.getMetaStatus());
    }
}






