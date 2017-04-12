package com.abc.test.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.test.utility.ReturnVO;
import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMeta;

@RestController
@RequestMapping("/wx")
public class WxController {
	
	private static Map<String, WxMeta> metaStorage = Collections.synchronizedMap(new HashMap<String, WxMeta>());
	
	@RequestMapping("index")
	ModelAndView index() {
		return new ModelAndView("wx");
	}
	
    @RequestMapping("createClient")
    ReturnVO createClient() throws IOException {
		//元数据
    	WxMeta meta = WxApp.start();
		metaStorage.put(meta.deviceId, meta);
		
		return ReturnVO.succeed(meta.deviceId);
    }
    
    @RequestMapping("outputQrcode/{deviceId}")
    void outputQrcode(@PathVariable("deviceId") String deviceId, HttpServletResponse response) throws IOException {
    	FileUtils.copyFile(metaStorage.get(deviceId).file_qrCode, response.getOutputStream());
    }
    
}






