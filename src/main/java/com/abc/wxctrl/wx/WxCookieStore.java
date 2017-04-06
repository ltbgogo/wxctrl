package com.abc.wxctrl.wx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.abc.utility.misc.JsonUtil;

import lombok.Getter;

public class WxCookieStore {

	@Getter
	private List<WxCookie> cookies = new ArrayList<WxCookie>();
	
	public void addCookie(WxCookie cookie) {
		if (this.cookies.contains(cookie)) {
			this.cookies.remove(cookie);
		}
		this.cookies.add(cookie);
	}
	
	public boolean isEmpty() {
		return this.cookies.isEmpty();
	}

	public String toString() {
		this.clearExpired();
		StringBuilder sb = new StringBuilder();
		for (WxCookie cookie : this.cookies) {
			sb.append(";");
			sb.append(cookie.getName()).append("=").append(cookie.getValue());
		}
		return sb.toString().replaceFirst(";", "");
	}
	
	public void clearExpired() {
		Date now = new Date();
		for (int i = 0; i < this.cookies.size(); i++) {
//			System.out.println(this.cookies.get(i).getName());
//			System.out.println(this.cookies.get(i).getExpires());
			if (this.cookies.get(i).getExpires() != null && this.cookies.get(i).getExpires().before(now)) {
				this.cookies.remove(i);
				i--;
			}
		}
	}
	
	public static void main(String[] args) {
		WxCookie cookie = new WxCookie("n1=\"v; \\\" \"; Version=1; Max-Age=60; Expires=Mon, 03-Apr-2017 01:52:45 GMT; Path=/tour/");
		System.out.println(JsonUtil.toPrettyJson(cookie));
	}
}
