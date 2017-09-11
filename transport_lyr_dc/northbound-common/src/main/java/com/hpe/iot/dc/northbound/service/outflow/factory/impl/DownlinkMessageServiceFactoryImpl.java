package com.hpe.iot.dc.northbound.service.outflow.factory.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.northbound.service.outflow.DownlinkMessageService;
import com.hpe.iot.dc.northbound.service.outflow.ExtendedDownlinkMessageService;
import com.hpe.iot.dc.northbound.service.outflow.factory.DownlinkMessageServiceFactory;
import com.hpe.iot.dc.service.ExtendedMessageService;
import com.hpe.iot.dc.service.MessageService;

/**
 * @author sveera
 *
 */
public class DownlinkMessageServiceFactoryImpl implements DownlinkMessageServiceFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, DownlinkMessageService> downlinkMessageServices = new HashMap<String, DownlinkMessageService>();
	private Map<String, MessageService> messageServices = new HashMap<String, MessageService>();

	public DownlinkMessageServiceFactoryImpl(final List<MessageService> allMessageServices) {
		for (MessageService service : allMessageServices) {
			handleForExtendedMessageService(service);
			handleForDownlinkMessageService(service);
			messageServices.put(service.getMessageType(), service);
		}
		logger.debug("Supported Downlink Message Service Types are " + downlinkMessageServices);
	}

	private void handleForDownlinkMessageService(MessageService service) {
		if (service instanceof DownlinkMessageService) {
			DownlinkMessageService upLinkMessageService = (DownlinkMessageService) service;
			downlinkMessageServices.put(service.getMessageType(), upLinkMessageService);
			handleForExtendedDownlinkMessageService(upLinkMessageService);
		}

	}

	private void handleForExtendedDownlinkMessageService(DownlinkMessageService upLinkMessageService) {
		if (upLinkMessageService instanceof ExtendedDownlinkMessageService) {
			for (String supportedMessageType : ((ExtendedDownlinkMessageService) upLinkMessageService)
					.getMessageTypes()) {
				downlinkMessageServices.put(supportedMessageType, upLinkMessageService);
			}
		}
	}

	private void handleForExtendedMessageService(MessageService service) {
		if (service instanceof ExtendedMessageService) {
			for (String supportedMessageType : ((ExtendedMessageService) service).getMessageTypes()) {
				messageServices.put(supportedMessageType, service);
			}
		}
	}

	@Override
	public MessageService getMessageService(String messageType) {
		return messageServices.get(messageType);
	}

	@Override
	public DownlinkMessageService getDownlinkMessageService(String messageType) {
		return downlinkMessageServices.get(messageType);
	}

	@Override
	public String toString() {
		return "DownlinkMessageServiceFactoryImpl [downlinkMessageServices=" + downlinkMessageServices
				+ ", messageServices=" + messageServices + "]";
	}

}
