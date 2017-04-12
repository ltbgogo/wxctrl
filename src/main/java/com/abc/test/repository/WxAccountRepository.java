package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.WxAccount;

public interface WxAccountRepository extends JpaRepository<WxAccount, String> {

}
