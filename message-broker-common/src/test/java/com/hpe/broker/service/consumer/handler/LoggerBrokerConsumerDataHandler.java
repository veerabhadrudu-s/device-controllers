package com.hpe.broker.service.consumer.handler;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;

/**
 * @author sveera
 *
 */
public class LoggerBrokerConsumerDataHandler implements BrokerConsumerDataHandler<String> {

	private final Logger logger = getLogger(getClass());
	private List<String> storedConsumerData = new CopyOnWriteArrayList<>();

	@Override
	public void handleConsumerMessage(String destination, String consumerData) {
		logger.info("Consumed Message by handler is " + consumerData);
		storedConsumerData.add(consumerData);
	}

	public List<String> getAllConsumedMessages() {
		return storedConsumerData;
	}

}
