package com.kafka.twitter.kafka.elastic.search;

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

//One consumer can connect to multiple topics
public class TweetsConsumerFromKafka {

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(TweetsConsumerFromKafka.class.getName());

		//	        String bootstrapServers = "127.0.0.1:9092";
		//	        String groupId = "my-first-application";
		//	        String topic = "first_topic";

		// create consumer configs
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, POCConstants.BOOTSTRAP_SERVERS);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, POCConstants.TWITTER_APPLICATION);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

		// subscribe consumer to our topic(s)
		consumer.subscribe(Arrays.asList(POCConstants.TWITTER_TWEETS_TOPIC));

		// poll for new data
		while(true){
			ConsumerRecords<String, String> records =
					consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

			for (ConsumerRecord<String, String> record : records){
				logger.info("Key: " + record.key() + ", Value: " + record.value());
				logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
			}
		}
	}
}
