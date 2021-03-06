package com.kafka.twitter.kafka.elastic.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.poc.util.KafkaProducerFactory;
import com.poc.util.POCConstants;
import com.poc.util.TwitterClientFactory;
import com.twitter.hbc.core.Client;

public class SourceTwitterTweetsOverToKafka {

	private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>(100000);
	private TwitterClientFactory twitterClientFactory = new TwitterClientFactory();
	private KafkaProducerFactory kafkaProducerFactory = new KafkaProducerFactory();

	public static void main(String a[]) {
		SourceTwitterTweetsOverToKafka sourceTwitterTweetsOverToKafka = new SourceTwitterTweetsOverToKafka();
		sourceTwitterTweetsOverToKafka.startStreaming();
	}

	private void startStreaming() {
		// Create a Twitter Client
		Client twitterClient = twitterClientFactory.createTwitterClient(messageQueue);

		// Attempt to connect
		twitterClient.connect();

		// Create a Kafka Producer
		KafkaProducer<String, String> kafkaProducer = kafkaProducerFactory.createKafkaProducer();

		// Loop to send tweets to Kafka
		consumeTweetsAndPublishOverToKafka(twitterClient, kafkaProducer);
	}

	private void consumeTweetsAndPublishOverToKafka(Client twitterClient, KafkaProducer<String, String> kafkaProducer) {
		while (!twitterClient.isDone()) {
			String msg = null;
			try {
				msg = messageQueue.poll(5, TimeUnit.SECONDS);
			} catch(Exception e) {
				e.printStackTrace();
				twitterClient.stop();
			}
			if(msg != null) {
				ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(POCConstants.TWITTER_TWEETS_TOPIC, msg);
				kafkaProducer.send(producerRecord, new Callback() {
					
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if(exception != null) {
							exception.printStackTrace();
						}
					}
				});
				System.out.println("Message -> "+msg);
				System.out.println("*** Published successfully over to Kafka ***");
			}
		}
	}
}
