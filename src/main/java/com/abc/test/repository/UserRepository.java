package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.User;
import com.abc.test.domain.WxUser;

public interface UserRepository extends JpaRepository<User, String> {

}
