package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.WxUser;

public interface WxUserRepository extends JpaRepository<WxUser, String> {

}
