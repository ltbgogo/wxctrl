package com.abc.test.service;

import static com.abc.test.repository.RepoFactory.f;

import java.util.Date;

import javax.transaction.Transactional;

import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

import com.abc.test.domain.WxAccount;
import com.abc.test.manager.SpringManager;
import com.abc.test.utility.spec.Processor;
import com.abc.test.wx.WxMeta;

@Transactional
@Service
public class TransactionService {

	@SneakyThrows
	public void processInTx(Processor processor) {
		processor.process();
	}
	
	public static void process(Processor processor) {
		SpringManager.getBean(TransactionService.class).processInTx(processor);
	}
}











