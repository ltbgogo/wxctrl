package com.abc.wxctrl.utility.db.repository;

import org.springframework.data.repository.NoRepositoryBean;

import com.abc.wxctrl.domain.WxAccount;
import com.abc.wxctrl.utility.db.domain.WxContactDomain;

@NoRepositoryBean
public interface WxContactDomainRepository<T extends WxContactDomain> extends IdDomainRepository<T> {

	T findByWxAccountAndSeq(WxAccount wxAccount, Long seq);
}
