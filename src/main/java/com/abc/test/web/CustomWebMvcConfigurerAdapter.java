package com.abc.test.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.h2.mvstore.DataUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.abc.test.wx.WxConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class CustomWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
	
	public static void main(String[] args) throws IOException {
		System.out.println(new File("F:\\wxctrl_data\\msg_img").getPath());
	}
	
	/**
	 * 静态资源映射
	 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	//微信图片消息映射路径
        registry.addResourceHandler("/wx_msg_img/**").addResourceLocations("file:" + WxConst.DATA_MSG_IMG_DIR + "//");
        super.addResourceHandlers(registry);
    }
	
    /**
     * 默认Index页映射
     */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("redirect:/index.html");
	}

	/**
	 * Json消息处理
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(simpleModule);
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
	}
}
