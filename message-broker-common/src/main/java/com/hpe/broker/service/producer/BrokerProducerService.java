package com.hpe.broker.service.producer;

import com.hpe.broker.service.BrokerConnectorService;

/**
 * @author sveera
 *
 */
public interface BrokerProducerService<V> extends BrokerConnectorService {

	void publishData(String destinationName, V value);
}
