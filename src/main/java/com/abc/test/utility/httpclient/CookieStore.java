package com.abc.test.utility.httpclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

public class CookieStore implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Getter
	private List<Cookie> cookies = new ArrayList<Cookie>();
	
	public void addCookie(Cookie cookie) {
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
		for (Cookie cookie : this.cookies) {
			sb.append(";");
			sb.append(cookie.getName()).append("=").append(cookie.getValue());
		}
		return sb.toString().replaceFirst(";", "");
	}
	
	public void clearExpired() {
		Date now = new Date();
		for (int i = 0; i < this.cookies.size(); i++) {
			if (this.cookies.get(i).getExpires() != null && this.cookies.get(i).getExpires().before(now)) {
				this.cookies.remove(i);
				i--;
			}
		}
	}
}
