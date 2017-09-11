package com.hpe.broker.service.consumer;

import com.hpe.broker.service.BrokerConnectorService;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;

/**
 * @author sveera
 *
 */
public interface BrokerConsumerService<V> extends BrokerConnectorService {

	void consumeData(String destination, BrokerConsumerDataHandler<V> brokerConsumerDataHandler);

}
