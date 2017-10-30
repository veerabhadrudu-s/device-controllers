package com.hpe.broker.service.consumer.handler;

/**
 * @author sveera
 *
 */
public interface BrokerConsumerDataHandler<T> {

	void handleConsumerMessage(String destiantion, T consumerData);
}
