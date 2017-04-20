package com.abc.test;


import javax.smartcardio.Card;

import lombok.SneakyThrows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.test.domain.WxAccount;
import com.abc.test.repository.WxAccountRepository;
import com.abc.test.utility.SpringManager;

@Service
public class Test {
	
	@SneakyThrows
	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
	    SpringApplication application = builder.build();
	    application.setWebEnvironment(false);
	    application.run(args);
	    
	    SpringManager.getBean(Test.class).test();
	}
	
	@Transactional()
	public void test() {
		WxAccountRepository r = SpringManager.getBean(WxAccountRepository.class);
		{
			WxAccount w = r.findOne("2");
			w.getMeta();
		}
		{
			
		}
	}
}








