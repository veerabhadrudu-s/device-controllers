/**
 * 
 */
package com.hpe.broker.service.consumer.factory.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.factory.BrokerConsumerServiceFactory;

/**
 * @author sveera
 *
 */
public class BrokerConsumerServiceFactoryImpl<V> implements BrokerConsumerServiceFactory<V> {

	private Map<String, BrokerConsumerService<V>> brokerConsumerServices = new ConcurrentHashMap<>();

	public BrokerConsumerServiceFactoryImpl(List<BrokerConsumerService<V>> brokerConsumerServices) {
		for (BrokerConsumerService<V> brokerConsumerService : brokerConsumerServices)
			this.brokerConsumerServices.put(brokerConsumerService.getName(), brokerConsumerService);
	}

	@Override
	public BrokerConsumerService<V> getBrokerConsumerService(String brokerConsumerServiceName) {
		return brokerConsumerServices.get(brokerConsumerServiceName);
	}

	@Override
	public void startAllServices() {
		for (Map.Entry<String, BrokerConsumerService<V>> brokerConsumerService : brokerConsumerServices.entrySet())
			brokerConsumerService.getValue().startService();
	}

	@Override
	public void stopAllServices() {
		for (Map.Entry<String, BrokerConsumerService<V>> brokerConsumerService : brokerConsumerServices.entrySet())
			brokerConsumerService.getValue().stopService();
	}

	@Override
	public String toString() {
		return "BrokerConsumerServiceFactoryImpl [brokerConsumerServices=" + brokerConsumerServices + "]";
	}

}
