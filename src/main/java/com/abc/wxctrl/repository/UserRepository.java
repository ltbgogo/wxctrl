package com.abc.wxctrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.wxctrl.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	User findByUsername(String username);
}
 