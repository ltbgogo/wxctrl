package com.abc.test.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abc.test.domain.WxAccount;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RepositoryTest {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WxAccountRepository wxAccountRepo;

	@Test
	public void findByUsernameShouldReturnUser() throws Exception {
//		userRepo.findAll();
//		WxAccount account = new WxAccount();
//		account.setId("1");
//		account.setNickName("bz");
//		account.setUin(1);
//		this.wxAccountRepo.save(account);
		System.out.println("*****************" + this.wxAccountRepo.findAll().size());
	}
}
