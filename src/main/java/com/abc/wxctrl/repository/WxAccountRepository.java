package com.abc.wxctrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.abc.wxctrl.domain.WxAccount;

public interface WxAccountRepository extends JpaRepository<WxAccount, String> {

	WxAccount findByUin(String uin);
}
