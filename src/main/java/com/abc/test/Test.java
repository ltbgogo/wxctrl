package com.abc.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

import lombok.Cleanup;

public class Test {

	public static void main(String[] args) throws IOException {
		FileChannel l = null;
		l.lock();
	}
}
