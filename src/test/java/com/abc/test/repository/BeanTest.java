package com.abc.test.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.abc.test.Application;
import com.abc.test.controller.WxController;
import com.abc.test.domain.WxAccount;
import com.abc.test.repository.WxAccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BeanTest {
	
	@Autowired
	private WxAccountRepository wxAccountRepo;

	@Test
	public void test1() {
		WxAccount account = new WxAccount();
		account.setId("1");
		account.setNickName("bz");
//		account.setUin(1);
		this.wxAccountRepo.save(account);
//		this.wxAccountRepo.findAll();
		System.out.println("*********************");
	}
}





