package com.abc.wxctrl.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.apache.commons.io.output.TeeOutputStream;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;

import com.abc.wxctrl.wx.WxMeta;

public class IOUtil {
	
	@SneakyThrows
	public static void forkConsoleOut(String filePath) {
		OutputStream out = new FileOutputStream(filePath, true);
		TeeOutputStream teeOutputStream = new TeeOutputStream(System.out, out);
		System.setOut( new PrintStream(teeOutputStream));
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

