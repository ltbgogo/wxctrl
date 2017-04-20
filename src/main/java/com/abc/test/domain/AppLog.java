package com.abc.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "t_app_log")
@Entity
public class AppLog {

	@Id
	@GeneratedValue
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_date")
	private Date eventDate;

	@Column(name = "level")
	private String level;
	
	@Column(name = "logger")
	private String logger;
	
	@Lob
	@Column(name = "message")
	private String message;
	
	@Lob
	@Column(name = "throwable")
	private String throwable;
}





