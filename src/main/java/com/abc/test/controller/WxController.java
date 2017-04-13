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
@RequestMapping("/wx")
public class WxController {
	
	private static Map<String, WxMeta> metaStorage = Collections.synchronizedMap(new HashMap<String, WxMeta>());
	@Autowired
	private WxAccountRepository wxAccountRepo;
	@Autowired
	private UserRepository userRepo;
	
	@RequestMapping("add")
	User add() {
		User u = new User();
		u.setId("xxxx");
		u = userRepo.save(u);
		return u;
	}
	
	@RequestMapping("index")
	ModelAndView index() {
		for (User user : userRepo.findAll()) {
			
			System.out.println(user.getId() + "**************");
		}
		return new ModelAndView("wx");
	}
	
    @RequestMapping("createClient")
    ReturnVO createClient() throws IOException {
		//元数据
    	WxMeta meta = WxApp.start();
		metaStorage.put(meta.getDeviceId(), meta);
		
		return ReturnVO.succeed(meta.getDeviceId());
    }
    
    @RequestMapping("outputQrcode/{deviceId}")
    void outputQrcode(@PathVariable("deviceId") String deviceId, HttpServletResponse response) throws IOException {
    	FileUtils.copyFile(metaStorage.get(deviceId).getFile_qrCode(), response.getOutputStream());
    }
    
}






