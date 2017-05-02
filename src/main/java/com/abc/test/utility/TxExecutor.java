package com.abc.test.utility;

import lombok.SneakyThrows;

import org.hibernate.jpa.event.internal.core.JpaAutoFlushEventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abc.test.manager.SpringManager;
import com.abc.test.utility.spec.Processor;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TxExecutor {

	@SneakyThrows
	public void executeInTx(Processor processor) {
		processor.process();
	}
	
	public static void execute(Processor processor) {
		SpringManager.getBean(TxExecutor.class).executeInTx(processor);
	}
}
