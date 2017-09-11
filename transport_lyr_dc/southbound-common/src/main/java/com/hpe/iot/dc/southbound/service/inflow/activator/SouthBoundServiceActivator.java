package com.hpe.iot.dc.southbound.service.inflow.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.service.inflow.UplinkMessageService;
import com.hpe.iot.dc.southbound.service.inflow.factory.UplinkMessageServiceFactory;

/**
 * @author sveera
 *
 */
public class SouthBoundServiceActivator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private UplinkMessageServiceFactory upLinkMessageServiceFactory;

	public SouthBoundServiceActivator(UplinkMessageServiceFactory upLinkMessageServiceFactory) {
		super();
		this.upLinkMessageServiceFactory = upLinkMessageServiceFactory;
	}

	public DeviceDataDeliveryStatus processMessage(final DeviceInfo payload) {
		logger.info("Message with Payload is " + payload);
		return executeService(payload);
	}

	private DeviceDataDeliveryStatus executeService(DeviceInfo payload) {
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = null;
		UplinkMessageService upLinkMessageService = upLinkMessageServiceFactory
				.getUpLinkMessageService(payload.getMessageType());
		if (upLinkMessageService != null) {
			logger.debug("Message will be processed by Uplink Message service " + upLinkMessageService);
			deviceDataDeliveryStatus = upLinkMessageService.executeService(payload);
		} else {
			MessageService generalMesageService = upLinkMessageServiceFactory
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
		return "SouthBoundServiceActivator [upLinkMessageServiceFactory=" + upLinkMessageServiceFactory + "]";
	}

}
