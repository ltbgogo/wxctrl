package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.WxMsg;

public interface WxMsgRepository extends JpaRepository<WxMsg, String> {

}
