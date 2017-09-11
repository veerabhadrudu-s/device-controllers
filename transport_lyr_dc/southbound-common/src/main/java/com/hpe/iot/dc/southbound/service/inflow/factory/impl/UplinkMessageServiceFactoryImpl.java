package com.hpe.iot.dc.southbound.service.inflow.factory.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.service.ExtendedMessageService;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.service.inflow.ExtendedUplinkMessageService;
import com.hpe.iot.dc.southbound.service.inflow.UplinkMessageService;
import com.hpe.iot.dc.southbound.service.inflow.factory.UplinkMessageServiceFactory;

/**
 * @author sveera
 *
 */
public class UplinkMessageServiceFactoryImpl implements UplinkMessageServiceFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, UplinkMessageService> upLinkMessageServices = new HashMap<String, UplinkMessageService>();
	private Map<String, MessageService> messageServices = new HashMap<String, MessageService>();

	public UplinkMessageServiceFactoryImpl(final List<MessageService> allMessageServices) {
		for (MessageService service : allMessageServices) {
			handleForExtendedMessageService(service);
			handleForUpLinkMessageService(service);
			messageServices.put(service.getMessageType(), service);
		}
		logger.debug("Supported Uplink Message Service Types are " + upLinkMessageServices);
	}

	private void handleForUpLinkMessageService(MessageService service) {
		if (service instanceof UplinkMessageService) {
			UplinkMessageService upLinkMessageService = (UplinkMessageService) service;
			upLinkMessageServices.put(service.getMessageType(), upLinkMessageService);
			handleForExtendedUplinkMessageService(upLinkMessageService);
		}

	}

	private void handleForExtendedUplinkMessageService(UplinkMessageService upLinkMessageService) {
		if (upLinkMessageService instanceof ExtendedUplinkMessageService) {
			for (String supportedMessageType : ((ExtendedUplinkMessageService) upLinkMessageService)
					.getMessageTypes()) {
				upLinkMessageServices.put(supportedMessageType, upLinkMessageService);
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
	public UplinkMessageService getUpLinkMessageService(String messageType) {
		return upLinkMessageServices.get(messageType);
	}

	@Override
	public String toString() {
		return "UplinkMessageServiceFactoryImpl [upLinkMessageServices=" + upLinkMessageServices + ", messageServices="
				+ messageServices + "]";
	}

}
