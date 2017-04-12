package com.abc.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abc.test.config.AppConfig;
import com.abc.test.repository.WxUserRepository;

@RestController
public class HomeController {
	
	@Autowired
	private WxUserRepository wxUserRepo;
	@Autowired
	private AppConfig appConfig;

    @RequestMapping("/")
    String home() {
        return "home";
    }
    
    @RequestMapping("/bb")
    ModelAndView aa() {
        return new ModelAndView("aa", "ttt", "cccc");
    }

}



