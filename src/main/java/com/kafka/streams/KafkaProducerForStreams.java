package com.kafka.streams;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import com.poc.util.POCConstants;


public class KafkaProducerForStreams {

	public static void main(String[] args) {

//        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, POCConstants.BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i=0; i<10; i++) {
        	produceAndFlushRecordImmediately(producer, i);
        }
        
        // flush and close producer
        producer.close();

    }

	private static void produceAndFlushRecordImmediately(KafkaProducer<String, String> producer, Integer counter) {
		// create a producer record
		ProducerRecord<String, String> record =
		        new ProducerRecord<String, String>("streams-plaintext-input", "jai shri ram: "+counter);

		// send data - asynchronous
		producer.send(record, new Callback() {
			
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				System.out.println("\n");
				System.out.println("*** Printing Record Metadata record ***");
				System.out.println("Topic: "+metadata.topic());
				System.out.println("Partition: "+metadata.partition());
				System.out.println("Offset: "+metadata.offset());
			}
		});

		// flush data : without this data will not go as send() in above line is asyn but main program close earlier
		producer.flush();
	}
}
