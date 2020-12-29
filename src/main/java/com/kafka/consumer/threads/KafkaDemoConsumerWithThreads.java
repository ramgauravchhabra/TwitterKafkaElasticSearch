package com.kafka.consumer.threads;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poc.util.POCConstants;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KafkaDemoConsumerWithThreads {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(KafkaDemoConsumerWithThreads.class.getName());
		
		// Latch with dealing with multiple thread
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		// Create the consumer
		Runnable myConsumerThread = new ConsumerThread(countDownLatch, POCConstants.FIRST_TOPIC);
		
		// Start the thread
		Thread thread = new Thread(myConsumerThread);
		thread.start();
		
		// Add shutdownhook
		Runtime.getRuntime().addShutdownHook(new Shutdownhook((ConsumerThread)myConsumerThread));
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			System.out.println("*** Application got intruptted ***");
		} finally {
			System.out.println("*** Application is closing ***");
		}
	}
}
