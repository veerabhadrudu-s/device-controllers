package com.hpe.broker.service.consumer.handler;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;

/**
 * @author sveera
 *
 */
public class LoggerBrokerConsumerDataHandler implements BrokerConsumerDataHandler<String> {

	private CountDownLatch countDownLatch;
	private final Logger logger = getLogger(getClass());
	private List<String> storedConsumerData = new CopyOnWriteArrayList<>();

	public LoggerBrokerConsumerDataHandler(CountDownLatch countDownLatch) {
		super();
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void handleConsumerMessage(String destination, String consumerData) {
		logger.trace("Consumed Message by handler is " + consumerData);
		storedConsumerData.add(consumerData);
		countDownLatch.countDown();
	}

	public List<String> getAllConsumedMessages() {
		return storedConsumerData;
	}

}
