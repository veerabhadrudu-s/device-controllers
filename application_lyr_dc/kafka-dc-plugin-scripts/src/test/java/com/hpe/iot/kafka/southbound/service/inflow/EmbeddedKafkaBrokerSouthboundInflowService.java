/**
 * 
 */
package com.hpe.iot.kafka.southbound.service.inflow;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.LoggerService;
import com.hpe.broker.service.kafka.KafkaBrokerService;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
public class EmbeddedKafkaBrokerSouthboundInflowService extends KafkaSouthboundInflowService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final KafkaBrokerService kafkaBrokerService;

	public EmbeddedKafkaBrokerSouthboundInflowService(String kafkaBrokerUrl, DeviceModelFactory deviceModelFactory,
			SouthboundService southboundService, ManagedExecutorService executorService,
			KafkaBrokerService kafkaBrokerService, LoggerService loggerService) {
		super(kafkaBrokerUrl, deviceModelFactory, southboundService, executorService, loggerService);
		this.kafkaBrokerService = kafkaBrokerService;
	}

	@Override
	public void startService() {
		kafkaBrokerService.startService();
		super.startService();
		logger.trace("Embedded kafka broker started sucessfully.");
	}

	@Override
	public void stopService() {
		super.stopService();
		kafkaBrokerService.stopService();
		logger.trace("Embedded kafka broker stopped sucessfully.");
	}

}
