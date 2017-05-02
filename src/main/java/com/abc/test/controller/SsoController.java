package com.abc.test.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.test.domain.User;
import com.abc.test.domain.WxAccount;
import com.abc.test.repository.UserRepository;
import com.abc.test.repository.WxAccountRepository;
import com.abc.test.utility.ReturnVO;
import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMeta;

@RestController
@RequestMapping("/sso")
public class SsoController {
	
	@Autowired
	private UserRepository userRepo;
	
	@RequestMapping("login")
	ModelAndView login(String userName, String password) {
		User user = this.userRepo.findByUserNameAndPassword(userName, password);
		if (user == null) {
			return new ModelAndView("/error/500", "r", ReturnVO.fail("用户名或密码错误！"));	
		} else {
			return new ModelAndView("redirect:/wx/index");	
		}
	}
}






