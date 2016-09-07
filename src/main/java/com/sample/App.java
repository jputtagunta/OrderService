package com.sample;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class App 
{
    public static void main( String[] args )
    {
    	final CountDownLatch latch = new CountDownLatch(2);
    	ExecutorService executor = Executors.newFixedThreadPool(2);
    	
    	Future<Integer> futureOfAA = executor.submit(create(new ServiceAA(), latch));
    	Future<Integer> futureOfB = executor.submit(create(new ServiceB(), latch));
    	
    	try {
			latch.await();
			System.out.println("Calls to Service-AA and Service-B are done");
			
			/*
			 * Here, Future.get() itself is a blocking call and it will block the current thread.
			 * So, there is no need of latch to synchronize the completion of 2 service calls.
			 * However, I left the latch part in place to show the idea expressed in the interview.
			 */
			int resultFromAA = futureOfAA.get();
			int resultFromB = futureOfB.get();
			
			executor.shutdown();
			
			System.out.println("Calling Service-C using resultFromAA and resultFromB");
			int resultFromC = resultFromAA + resultFromB; // assuming this operation is Service-C
			System.out.println("Result from Service-C is: "+ resultFromC);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    }
    
    private static ServiceCaller create(Service service, CountDownLatch latch){
    	return new ServiceCaller(service, latch);
    }
}

class ServiceAA implements Service {

	@Override
	public Integer execute() {
		System.out.println("Calling Service-A");
		int resultFromA = callServiceA();
		
		System.out.println("Calling Service-AA using the result from Service-A");
		int resultFromAA = callServiceAA(resultFromA);
		
		return resultFromAA;
	}
	
	private int callServiceAA(int resultFromA) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return resultFromA * resultFromA;
	}

	private int callServiceA() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 10;
	}

	@Override
	public String getName() {
		return "Service-AA";
	}
}

class ServiceB implements Service {

	@Override
	public Integer execute() {
		try{
			System.out.println("Calling Service-B");
			int resultFromB = 25; //assuming this is obtained here from Service-B
			Thread.sleep(1000);
			return resultFromB;
		}
		catch(InterruptedException e){
			System.out.println("Service-B execution was interrupted");
		}
		return null;
	}

	@Override
	public String getName() {
		return "Service-B";
	}
}
