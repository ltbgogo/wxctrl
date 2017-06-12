package com.abc.wxctrl.utility;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultVO {
	
	public static final ResultVO SUCCESS = ResultVO.succeed(null);
	public static final ResultVO FAILURE = ResultVO.fail(null);
	
	public static ResultVO fail(Object Content) {
		return new ResultVO(false, Content);
	}
	
	public static ResultVO succeed(Object Content) {
		return new ResultVO(true, Content);
	}
	
	private boolean success;
	private Object content;
	
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public <T> T getContent(Class<T> requiredType) {
		return (T) this.getContent();
	}
}
