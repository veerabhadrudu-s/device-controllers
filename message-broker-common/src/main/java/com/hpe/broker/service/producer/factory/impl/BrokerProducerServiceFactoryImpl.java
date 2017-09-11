/**
 * 
 */
package com.hpe.broker.service.producer.factory.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.broker.service.producer.BrokerProducerService;
import com.hpe.broker.service.producer.factory.BrokerProducerServiceFactory;

/**
 * @author sveera
 *
 */
public class BrokerProducerServiceFactoryImpl<V> implements BrokerProducerServiceFactory<V> {

	private Map<String, BrokerProducerService<V>> brokerProducerServices = new ConcurrentHashMap<>();

	public BrokerProducerServiceFactoryImpl(List<BrokerProducerService<V>> brokerProducerServices) {
		for (BrokerProducerService<V> brokerProducerService : brokerProducerServices)
			this.brokerProducerServices.put(brokerProducerService.getName(), brokerProducerService);
	}

	@Override
	public BrokerProducerService<V> getBrokerProducerService(String brokerProducerServiceName) {
		return brokerProducerServices.get(brokerProducerServiceName);
	}

	public Map<String, BrokerProducerService<V>> getBrokerProducerServices() {
		return brokerProducerServices;
	}

	@Override
	public void startAllServices() {
		for (Map.Entry<String, BrokerProducerService<V>> brokerProducerService : brokerProducerServices.entrySet())
			brokerProducerService.getValue().startService();
	}

	@Override
	public void stopAllServices() {
		for (Map.Entry<String, BrokerProducerService<V>> brokerProducerService : brokerProducerServices.entrySet())
			brokerProducerService.getValue().stopService();
	}

	@Override
	public String toString() {
		return "BrokerProducerServiceFactoryImpl [brokerProducerServices=" + brokerProducerServices + "]";
	}

}
