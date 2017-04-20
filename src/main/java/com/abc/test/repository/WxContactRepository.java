package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.WxAccount;
import com.abc.test.domain.WxContact;

public interface WxContactRepository extends JpaRepository<WxContact, String> {

	WxAccount findByUserName(String userName);
}
