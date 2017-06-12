package com.abc.wxctrl.utility;

import static com.abc.wxctrl.repository.RepoFactory.rf;

import java.util.Date;

import javax.transaction.Transactional;

import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.manager.SpringManager;
import com.abc.wxctrl.utility.spec.Processor;
import com.abc.wxctrl.wx.WxMeta;

@Transactional
@Service
public class TxExecutor {

	@SneakyThrows
	public void processInTx(Processor processor) {
		processor.process();
	}
	
	public static void process(Processor processor) {
		SpringManager.getBean(TxExecutor.class).processInTx(processor);
	}
}











