package com.abc.test;

import lombok.SneakyThrows;

import com.abc.test.domain.WxAccount;
import com.abc.test.repository.WxAccountRepository;
import com.abc.test.utility.SpringManager;
import com.abc.test.utility.TxExecutor;
import com.abc.test.utility.spec.Processor;

public class Test {
	
	@SneakyThrows
	public static void main(String[] args) {
		SpringManager.startMailApplication(Application.class, args);
	    
	    TxExecutor.execute(new Processor() {
			@Override
			public void process() throws Exception {
				WxAccountRepository r = SpringManager.getBean(WxAccountRepository.class);
				{
					WxAccount w = r.findOne("8");
					System.out.println(w.getOwner().getId());
				}
				{
					
				}
			}
		});
	}
}








