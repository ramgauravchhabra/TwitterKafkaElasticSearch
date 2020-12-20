package com.poc.util;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterClientFactory {

	// Read from YAML properties file defined @ src\\main\\resources\\poc_properties.yaml file
	private static String consumerKey = PropertiesProvider.getValueFor(POCConstants.CONSUMER_KEY);
	private static String consumerSecret = PropertiesProvider.getValueFor(POCConstants.CONSUMER_SECRET);
	private static String token = PropertiesProvider.getValueFor(POCConstants.TOKEN);
	private static String tokenSecret = PropertiesProvider.getValueFor(POCConstants.TOKEN_SECRET);

	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		//		List<Long> followings = Lists.newArrayList(1234L, 566788L);
		List<String> terms = Lists.newArrayList(POCConstants.BITCOIN);
		//		hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

		ClientBuilder builder = new ClientBuilder()
				.name(POCConstants.CLIENT_NAME)                              // optional: mainly for the logs
				.hosts(hosebirdHosts)
				.authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		//				  .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

		Client twitterClient = builder.build();
		// Attempts to establish a connection.
		return twitterClient;
	}
}
