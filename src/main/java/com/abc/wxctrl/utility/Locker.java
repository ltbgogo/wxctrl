package com.abc.wxctrl.utility;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * 注意，锁的声明应该放在代码块的开头
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Locker implements Closeable {
	
	public static void main(String[] args) {
		final StringBuffer buff = new StringBuffer();
		for (int i = 0; i < 100; i++) {
			new Thread() {
				@SneakyThrows
				public void run() {
					if (!buff.toString().equals("aa")) {
						@Cleanup
						Locker locker = Locker.lock("xxx");
						if (!buff.toString().equals("aa")) {
							buff.append("aa");	
							System.out.println("ddddd");
						}
					}
				}
			}.start();
		}	
	}
	
	private static Map<String, Locker> locksMap = Collections.synchronizedMap(new HashMap<String, Locker>());
	
	public static Locker lock(String key) {
		Locker locker = null;
		synchronized (locksMap) {
			if (!locksMap.containsKey(key)) {
				locksMap.put(key, new Locker(key, new ReentrantLock()));
			} 
			locker = locksMap.get(key);	
		}
		locker.lock.lock();
		return locker;
	}
	
	private String key;
	private ReentrantLock lock;

	@Override
	public void close() {
		try {
			this.lock.unlock();
		} finally {
			locksMap.remove(this.key);
		}
	}
}
