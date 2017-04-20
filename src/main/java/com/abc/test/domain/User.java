package com.abc.test.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_user")
@Entity
public class User {

	@Id
	@GeneratedValue
	private String id;
	/**
	 * 拥有的微信账号
	 */
	@OneToMany(mappedBy = "owner")
	private List<WxAccount> wxAccounts = new ArrayList<>();
}





