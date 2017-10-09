/**
 * 
 */
package com.hpe.iot.mqtt.northbound.sdk.handler.mock;

import com.hpe.broker.service.producer.BrokerProducerService;

/**
 * @author sveera
 *
 */
public class MockNorthboundDownlinkProducerService {

	private final String destination;
	private final BrokerProducerService<String> brokerProducerService;

	public MockNorthboundDownlinkProducerService(String destination,
			BrokerProducerService<String> brokerConsumerService) {
		super();
		this.destination = destination;
		this.brokerProducerService = brokerConsumerService;
	}

	public void startService() {
		brokerProducerService.startService();
	}

	public void stopService() {
		brokerProducerService.stopService();
	}

	public void publishDownlinkData(String downlinkCommandData) {
		brokerProducerService.publishData(destination, downlinkCommandData);
	}

}
