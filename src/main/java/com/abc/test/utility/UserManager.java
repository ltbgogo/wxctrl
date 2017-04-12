package com.abc.test.utility;

import com.abc.test.domain.User;
import com.abc.test.repository.RepoFactory;

public class UserManager {

	public static User getUser() {
		return RepoFactory.f.getUserRepo().findOne("1");
	}
}
