package com.abc.wxctrl.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MsgVO {

	private String code;
	private Object content;
	
	public boolean isCode(String code) {
		return this.getCode().equals(code);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getContent(Class<T> requiredType) {
		return (T) this.getContent();
	}
	
	public static MsgVO ofJson(String jsonString) {
		JSONObject json = JSON.parseObject(jsonString);
		return new MsgVO(json.getString("code"), json.get("content"));
	}
}











