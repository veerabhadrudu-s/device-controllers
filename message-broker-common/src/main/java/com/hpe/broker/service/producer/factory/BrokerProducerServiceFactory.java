/**
 * 
 */
package com.hpe.broker.service.producer.factory;

import com.hpe.broker.service.producer.BrokerProducerService;

/**
 * @author sveera
 *
 */
public interface BrokerProducerServiceFactory<V> {

	BrokerProducerService<V> getBrokerProducerService(String brokerProducerServiceName);

	void startAllServices();

	void stopAllServices();
}
