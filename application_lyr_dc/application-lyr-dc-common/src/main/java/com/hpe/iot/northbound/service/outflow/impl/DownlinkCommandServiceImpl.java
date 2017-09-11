/**
 * 
 */
package com.hpe.iot.northbound.service.outflow.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.factory.BrokerConsumerServiceFactory;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandService;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandServiceHandler;

/**
 * @author sveera
 *
 */
public class DownlinkCommandServiceImpl implements DownlinkCommandService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final DownlinkCommandServiceHandler downlinkCommandServiceHandler;
	private final BrokerConsumerService<String> brokerConsumerService;
	private final String downlinkConsumerDestination;

	public DownlinkCommandServiceImpl(DownlinkCommandServiceHandler downlinkCommandServiceHandler,
			BrokerConsumerServiceFactory<String> brokerConsumerServiceFactory, String activeMessageBroker,
			String downlinkConsumerDestination) {
		super();
		this.downlinkCommandServiceHandler = downlinkCommandServiceHandler;
		this.downlinkConsumerDestination = downlinkConsumerDestination;
		brokerConsumerService = brokerConsumerServiceFactory.getBrokerConsumerService(activeMessageBroker);
	}

	@Override
	public void startService() {
		brokerConsumerService.startService();
		BrokerConsumerDataHandler<String> brokerConsumerDataHandler = new BrokerConsumerDataHandlerImpl(
				downlinkCommandServiceHandler);
		brokerConsumerService.consumeData(downlinkConsumerDestination, brokerConsumerDataHandler);
		
	}

	@Override
	public void stopService() {
		brokerConsumerService.stopService();

	}

	private class BrokerConsumerDataHandlerImpl implements BrokerConsumerDataHandler<String> {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final DownlinkCommandServiceHandler northboundService;

		public BrokerConsumerDataHandlerImpl(DownlinkCommandServiceHandler northboundService) {
			this.northboundService = northboundService;
		}

		@Override
		public void handleConsumerMessage(String consumerData) {
			logger.trace("Received downlink message from message broker is " + consumerData);

		}

	}

}
