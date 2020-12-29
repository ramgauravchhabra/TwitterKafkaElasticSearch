package com.kafka.consumer.threads;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poc.util.POCConstants;

public class ConsumerThread implements Runnable {

	private CountDownLatch countDownLatch;
	private KafkaConsumer<String, String> kafkaConsumer;
	Logger logger = LoggerFactory.getLogger(ConsumerThread.class.getName());
	
	public ConsumerThread(CountDownLatch countDownLatch, String topic) {
		this.countDownLatch = countDownLatch; //Will do countdown in finally block
		
		// Create Consumer
		Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, POCConstants.BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, POCConstants.MY_FIRST_APPLICATION);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		this.kafkaConsumer = new KafkaConsumer<String, String>(properties); // We can pass properties/map etc. also
		
		// Subscribe consumer to topic
		this.kafkaConsumer.subscribe(Arrays.asList(topic));
	}

	public void run() {
		try {
			while(true){
	            ConsumerRecords<String, String> records =
	            		kafkaConsumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

	            for (ConsumerRecord<String, String> record : records){
	                logger.info("Key: " + record.key() + ", Value: " + record.value());
	                logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
	            }
	        }
		} catch(WakeupException we) {
			 logger.info("Received shutdown signal!");
		} finally {
			kafkaConsumer.close();
            // tell our main code we're done with the consumer
			countDownLatch.countDown();
		}
	}
	
	public void shutdown() {
        // The wakeup() method is a special method to interrupt consumer.poll()
        // it will throw the exception WakeUpException
		logger.info("*** Shutdown being called ***");
		kafkaConsumer.wakeup();
    }
}
