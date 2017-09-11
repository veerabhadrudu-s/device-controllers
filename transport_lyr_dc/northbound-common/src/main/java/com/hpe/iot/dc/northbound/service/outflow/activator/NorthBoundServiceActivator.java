package com.hpe.iot.dc.northbound.service.outflow.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.northbound.service.outflow.DownlinkMessageService;
import com.hpe.iot.dc.northbound.service.outflow.factory.DownlinkMessageServiceFactory;
import com.hpe.iot.dc.service.MessageService;

/**
 * @author sveera
 *
 */
public class NorthBoundServiceActivator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private DownlinkMessageServiceFactory downlinkMessageServiceFactory;

	public NorthBoundServiceActivator(DownlinkMessageServiceFactory upLinkMessageServiceFactory) {
		super();
		this.downlinkMessageServiceFactory = upLinkMessageServiceFactory;
	}

	public DeviceDataDeliveryStatus processMessage(final DeviceInfo payload) {
		logger.info("Message with Payload is " + payload);
		return executeService(payload);
	}

	private DeviceDataDeliveryStatus executeService(DeviceInfo payload) {
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = null;
		DownlinkMessageService downlinkMessageService = downlinkMessageServiceFactory
				.getDownlinkMessageService(payload.getMessageType());
		if (downlinkMessageService != null) {
			logger.debug("Message will be processed by Uplink Message service " + downlinkMessageService);
			deviceDataDeliveryStatus = downlinkMessageService.executeService(payload);
		} else {
			MessageService generalMesageService = downlinkMessageServiceFactory
					.getMessageService(payload.getMessageType());
			if (generalMesageService != null) {
				logger.debug("Message will be processed by General Message service " + generalMesageService);
				deviceDataDeliveryStatus = generalMesageService.executeService(payload);
			} else {
				logger.warn("Message Service could not be found for the message Type " + payload.getMessageType());
			}
		}
		return deviceDataDeliveryStatus;
	}

	@Override
	public String toString() {
		return "NorthBoundServiceActivator [downlinkMessageServiceFactory=" + downlinkMessageServiceFactory + "]";
	}

}
