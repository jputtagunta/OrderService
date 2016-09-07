package com.sample;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class ServiceCaller implements Callable<Integer>{

	private final Service service;
	private final CountDownLatch latch;
	
	public ServiceCaller(Service service, CountDownLatch latch) {
		this.service = service;
		this.latch = latch;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("Started Executing: "+ service.getName());
		Integer result = service.execute();
		System.out.println("Finished Executing: "+ service.getName());
		latch.countDown();
		return result;
	}
}
