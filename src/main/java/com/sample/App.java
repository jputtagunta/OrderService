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
		try{
			System.out.println("Calling Service-A");
			int resultFromA = 10; //assuming this is obtained here from Service-A
			Thread.sleep(1000);
			
			System.out.println("Calling Service-AA using the result from Service-A");
			
			int resultFromAA = resultFromA * resultFromA; //assuming resultFromA is passed to Service AA
			Thread.sleep(1000);
			
			return resultFromAA;
		}
		catch(InterruptedException e){
			System.out.println("Service-AA execution was interrupted.");
		}
		return null;
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
