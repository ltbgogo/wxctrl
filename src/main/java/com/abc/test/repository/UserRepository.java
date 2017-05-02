package com.abc.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.test.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	User findByUserNameAndPassword(String userName, String password);
}
 