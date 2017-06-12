package com.abc.wxctrl.utility.db.repository;

import org.springframework.data.repository.NoRepositoryBean;

import com.abc.wxctrl.utility.db.domain.WxMsgDomain;

@NoRepositoryBean
public interface WxMsgDomainRepository<T extends WxMsgDomain> extends IdDomainRepository<T> {
	
	T findByMsgId(String msgId);
}




