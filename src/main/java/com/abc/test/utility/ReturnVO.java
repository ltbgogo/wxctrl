package com.abc.test.utility;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReturnVO {
	
	public static final ReturnVO SUCCESS = ReturnVO.succeed(null);
	public static final ReturnVO FAILURE = ReturnVO.fail(null);
	
	public static ReturnVO fail(Object result) {
		return new ReturnVO(false, result);
	}
	
	public static ReturnVO succeed(Object result) {
		return new ReturnVO(true, result);
	}
	
	private boolean success;
	private Object result;
	
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public <T> T getResult(Class<T> requiredType) {
		return (T) this.result;
	}
	
	@JsonIgnore
	public String getStringResult() {
		return (String) this.result;
	}
}
