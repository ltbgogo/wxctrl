package com.abc.wxctrl.wx;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.SneakyThrows;

import org.w3c.dom.Element;

import com.abc.utility.misc.XmlUtil;

/**
 * <error>
		<ret>0</ret>
		<message></message>
		<skey>@crypt_b26ec12f_7f34c428641978f2f285a93d466ce2b9</skey>
		<wxsid>/sugTCKHCHFOZp4u</wxsid>
		<wxuin>1596172085</wxuin>
		<pass_ticket>wmYXS9nzwLznN%2FLeL%2BKQOG8ova630nr5HPmyDJEjRB861Xu%2FAiB6niZipJUCw0%2FZ</pass_ticket>
		<isgrayscale>1</isgrayscale>
	</error>
 */
@Getter
public class WxAccessToken {
	
	private int ret;
	private String message;
	private String skey;
	private String wxsid;
	private String wxuin;
	private String pass_ticket;
	private String isgrayscale;
	private String deviceId;
	
	public WxAccessToken setData(String xml, String deviceId) {
		Element root = XmlUtil.XU.getDocument(xml).getDocumentElement();
		this.wxuin = XmlUtil.XU.getString("wxuin", root);
		this.wxsid = XmlUtil.XU.getString("wxsid", root);
		this.skey = XmlUtil.XU.getString("skey", root);
		this.pass_ticket = XmlUtil.XU.getString("pass_ticket", root);
		this.deviceId = deviceId;
		return this;
	}
	
	public Map<String, Object> toBaseRequest() {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
    	data.put("Uin", this.wxuin);
    	data.put("Sid", this.wxsid);
    	data.put("Skey", this.skey);
   		data.put("DeviceID", this.deviceId);
   		return data;
	}
}




