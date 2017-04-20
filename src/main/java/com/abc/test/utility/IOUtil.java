package com.abc.test.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;

import com.abc.test.wx.WxMeta;

public class IOUtil {
	
	public static void main(String[] args) {
		WxMeta a = new WxMeta();
		a.setBase_uri("xxx");
		byte[] d = serialize(a);
		a = deserialize(d);
		System.out.println(a.getBase_uri());
	}

	/**
	 * 序列化对象
	 */
	@SneakyThrows
	public static byte[] serialize(Serializable o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);    //写入customer对象
		oos.flush();
		oos.close();
		return bos.toByteArray();
	}
	
	/**
	 * 反序列化对象
	 */
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public static <T> T deserialize(byte[] b) {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
		T o = (T) ois.readObject();
		return o;
	}
}

@AllArgsConstructor
@Data
class Person implements Serializable{
    private String name;
    private int age;
}

