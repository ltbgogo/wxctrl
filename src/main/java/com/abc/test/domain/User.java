package com.abc.test.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "t_user")
public class User {

	@Id
	@GeneratedValue
	private String id;
}





