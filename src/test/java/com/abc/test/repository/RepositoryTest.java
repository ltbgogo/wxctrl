package com.abc.test.repository;

import java.io.IOException;

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
import com.abc.test.wx.WxApp;
import com.abc.test.wx.WxMeta;

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
		WxMeta meta = WxApp.start();
		Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start " + meta.getFile_qrCode()});
	}
}
