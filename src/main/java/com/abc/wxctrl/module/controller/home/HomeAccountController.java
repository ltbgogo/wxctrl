package com.abc.wxctrl.module.controller.home;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.wxctrl.manager.UserManager;
import com.abc.wxctrl.manager.UserManager.UserConst;
import com.abc.wxctrl.utility.ResultVO;
import com.abc.wxctrl.wx.WxApp;
import com.abc.wxctrl.wx.WxMeta;
import com.abc.wxctrl.wx.WxMeta.WxMetaStatus;

@RestController
@RequestMapping("/wx/home/account")
public class HomeAccountController {
	
	@Autowired
	private WxApp wxApp;
	
    @RequestMapping("listAccount")
    ModelAndView listAccount() {
    	return new ModelAndView("/wx/home/account_list", "r", ResultVO.succeed(UserManager.getCurrent().getWxAccounts()));
    }
	
    /**
     * 添加一个微信账号
     * @return
     */
    @RequestMapping("addAccount")
    ResultVO addAccount() {
    	WxMeta meta = UserManager.getUserData(UserConst.KEY_LOGIN_META);
    	//假如当前用户没有正在准备登陆的微信账号，就获取一个登陆扫描码
    	if (!(meta != null && meta.getMetaStatus() == WxMetaStatus.waitForLogin)) {
    		UserManager.setUserData(UserConst.KEY_LOGIN_META, this.wxApp.startOne());
    	}
    	return ResultVO.SUCCESS;
    }
    
    /**
     * 输出登录扫描码
     */
    @RequestMapping("outputQrcode")
    void outputQrcode(HttpServletResponse response) throws IOException {
    	FileUtils.copyFile(UserManager.getUserData(UserConst.KEY_LOGIN_META, WxMeta.class).getQrCodeImg(), response.getOutputStream());
    }
    
    /**
     * 查看登录状态
     */
    @RequestMapping("checkLoginStatus")
    ResultVO checkLoginStatus() {
    	WxMeta meta = UserManager.getUserData(UserConst.KEY_LOGIN_META, WxMeta.class);
    	return ResultVO.succeed(meta.getMetaStatus());
    }
}






