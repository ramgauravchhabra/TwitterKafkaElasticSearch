package com.kafka.consumer.threads;

public class Shutdownhook extends Thread {

	private ConsumerThread consumerThread;
	
	public Shutdownhook(ConsumerThread consumerThread) {
		this.consumerThread = consumerThread;
	}
	
	public void run() {
        System.out.println("*** In Shutdownhook : Calling shutdown hook ***");
        consumerThread.shutdown();
     }
}
