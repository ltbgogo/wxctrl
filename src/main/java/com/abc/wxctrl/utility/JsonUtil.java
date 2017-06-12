package com.abc.wxctrl.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> toList(JSONArray array, Class<T> requiredType) {
		return  ((List) array);
	}
	
	public static <T> T parseJson(String json, Class<T> requiredType) {
		return JSON.parseObject(json, requiredType);
	}
	
	public static JSONObject toMap(JSONArray array, String keyElement) {
		JSONObject map = new JSONObject();
		for (JSONObject object : JsonUtil.toList(array, JSONObject.class)) {
			map.put(object.getString(keyElement), object);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T parseJson(String json) {
		return (T) JSON.parse(json);
	}
	
	public static String toJson(Object o) {
		return JSON.toJSONString(o);
	}
	
	public static String toPrettyJson(Object o) {
		return JSON.toJSONString(o, SerializerFeature.PrettyFormat);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> bean2Map(Object u) {
		return parseJson(toJson(u), Map.class);
	}
	
	public static JSONArray searchObjects(JSONArray jsonObjectArray, String jsonObjectKey, Object jsonObjectValue) {
		JSONArray r = new JSONArray();
		for (JSONObject o : JsonUtil.toList(jsonObjectArray, JSONObject.class)) {
			if (jsonObjectValue.equals(o.getObject(jsonObjectKey, jsonObjectValue.getClass()))) {
				r.add(o);
			}
		}
		return r;
	}
	
	public static JSONObject searchObject(JSONArray jsonObjectArray, String jsonObjectKey, Object jsonObjectValue) {
		JSONArray r = new JSONArray();
		for (JSONObject o : JsonUtil.toList(jsonObjectArray, JSONObject.class)) {
			if (jsonObjectValue.equals(o.getObject(jsonObjectKey, jsonObjectValue.getClass()))) {
				return o;
			}
		}
		return null;
	}
}





