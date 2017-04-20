package com.abc.test.utility;

import java.sql.Connection;
import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import lombok.SneakyThrows;

public class ConnectionFactory {

	
	
	@SneakyThrows
	public static Connection getConnection() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
//		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl("jdbc:h2:tcp://localhost:9092/d:/db/h2/test");
		ds.setUsername("test");
		ds.setPassword("test");
		
		return ds.getConnection();
	}
}
