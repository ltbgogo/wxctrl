package com.abc.test.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.test.config.AppConfig;
import com.abc.test.repository.WxUserRepository;
import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMsgListener;
import com.abc.test.wx.WxMeta;
import com.abc.test.wx.WxService;

@RestController
@RequestMapping("/wx")
public class WxController {
	
	private static Map<String, WxMeta> metaStorage = Collections.synchronizedMap(new HashMap<String, WxMeta>());
	
	@RequestMapping("index")
	ModelAndView index() {
		return new ModelAndView("index");
	}
	
    @RequestMapping("createClient")
    String createClient() throws IOException {
		//元数据
    	WxMeta meta = WxApp.start();
		metaStorage.put(meta.deviceId, meta);
		
		return meta.uuid;
    }
    
    
    
}






