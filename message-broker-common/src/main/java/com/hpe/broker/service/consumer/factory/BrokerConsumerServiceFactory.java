/**
 * 
 */
package com.hpe.broker.service.consumer.factory;

import com.hpe.broker.service.consumer.BrokerConsumerService;

/**
 * @author sveera
 *
 */
public interface BrokerConsumerServiceFactory<V> {

	BrokerConsumerService<V> getBrokerConsumerService(String brokerConsumerServiceName);

	void startAllServices();

	void stopAllServices();
}
