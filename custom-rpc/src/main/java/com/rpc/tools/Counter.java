package com.rpc.tools;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
	
	private static AtomicLong counter = new AtomicLong(Constant._0);

	public static long addOne() {
		return counter.incrementAndGet();
	}
}