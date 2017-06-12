package com.abc.wxctrl.utility.httpclient;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.SneakyThrows;

import org.apache.commons.lang3.StringUtils;

import com.abc.wxctrl.utility.DateUtil;
import com.abc.wxctrl.utility.StringUtil;

@Data
public class Cookie implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@SneakyThrows
	public Cookie(String cookieStr) {
		//JSESSIONID=BE664F0C3675FD0DB0572A79CC7EF29C; Path=/tour/; HttpOnly
		//"n1=v1; Expires=Mon, 03-Apr-2017 00:40:24 GMT; Path=/tour/"
		//"n3=v3; Path=/tour/"
		//e32=\"15901352160e0SUR/fM1/2Xy/qEUpGX1k2hrBrrccDBM0eAoUInL4hU=\"; Version=1; Max-Age=604800; Expires=Sun, 09-Apr-2017 00:14:00 GMT; Path=/tour/
		cookieStr = StringUtil.encodeQuoted(cookieStr, false);
		String[] arr = cookieStr.split("; ");
		for (int i = 0; i < arr.length; i++) {
			String item = arr[i];
			String before = StringUtils.substringBefore(item, "=");
			String after = StringUtils.substringAfter(item, "=");
			if (i == 0) {
				this.setName(before);
				this.setValue(StringUtil.decodeQuoted(after, "\""));
			} else {
				if (item.startsWith("Path=")) {
					this.setPath(after);
				} else if (item.equals("HttpOnly")) {
					this.setHttpOnly(true);
				} else if (item.startsWith("Max-Age=")) {
					System.out.println(this.getName());
					System.out.println(new Date());
					this.setExpires(DateUtil.offset(Integer.valueOf(after) * 1000));
					System.out.println(this.getExpires());
				} else if (item.startsWith("Expires=")) {
					this.setExpires(DateUtil.parseGMT(after));
				}
			}
		}	
	}
	
	/**
	 * Cookie名称
	 * Cookie名称必须使用只能用在URL中的字符，一般用字母及数字，不能包含特殊字符，如有特殊字符想要转码。
	 * 如js操作cookie的时候可以使用escape()对名称转码。
	 */
	private String name;
	/**
	 * Cookie值
	 * Cookie值同理Cookie的名称，可以进行转码和加密。
	 */
	private String value;
	/**
	 * 过期日期
	 * 一个GMT格式的时间，当过了这个日期之后，浏览器就会将这个Cookie删除掉，当不设置这个的时候，Cookie在浏览器关闭后消失。
	 */
	private Date expires;
	/**
	 * 子域
	 * 指定在该子域下才可以访问Cookie，例如要让Cookie在a.test.com下可以访问，
	 * 但在b.test.com下不能访问，则可将domain设置成a.test.com。
	 */
	private String domain;
	/**
	 * 一个路径
	 * 在这个路径下面的页面才可以访问该Cookie，一般设为“/”，以表示同一个站点的所有页面都可以访问这个Cookie。
	 */
	private String path;
	/**
	 * 安全性
	 * 指定Cookie是否只能通过https协议访问，一般的Cookie使用HTTP协议既可访问，
	 * 如果设置了Secure（没有值），则只有当使用https协议连接时cookie才可以被页面访问。
	 */
	private Boolean secure;
	/**
	 * 如果在Cookie中设置了"HttpOnly"属性，那么通过程序(JS脚本、Applet等)将无法读取到Cookie信息。
	 */
	private Boolean httpOnly;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cookie other = (Cookie) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
