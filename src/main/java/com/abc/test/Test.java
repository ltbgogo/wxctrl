package com.abc.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import lombok.Cleanup;

public class Test {

	public static void main(String[] args) throws IOException {
		Random r = new Random();
		@Cleanup
		DataOutputStream out = new DataOutputStream(new FileOutputStream("d://test//data.log"));
		for (int i = 400 * 400 * 1000; i > 0; i--) {
			System.out.println(i);
			out.writeFloat(r.nextFloat());
		}
	}
}
