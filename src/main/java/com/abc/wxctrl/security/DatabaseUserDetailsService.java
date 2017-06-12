package com.abc.wxctrl.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import static com.abc.wxctrl.domain.User.USER_AUTHORITY;
import static com.abc.wxctrl.repository.RepoFactory.rf;

@Component
@Transactional
public class DatabaseUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if (rf.getUserRepo().findByUsername(username) == null) {
			return new User(username, "", new ArrayList<SimpleGrantedAuthority>());
		} else {
			return new User(username, rf.getUserRepo().findByUsername(username).getPassword(), Arrays.asList(new SimpleGrantedAuthority(USER_AUTHORITY)));	
		}
	}
}






