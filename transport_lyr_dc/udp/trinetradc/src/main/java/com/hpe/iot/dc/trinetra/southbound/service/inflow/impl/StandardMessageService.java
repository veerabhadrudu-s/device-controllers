package com.hpe.iot.dc.trinetra.southbound.service.inflow.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.dao.DeviceDaoRepository;
import com.hpe.iot.dc.model.DeviceData;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.dc.southbound.service.inflow.ExtendedUplinkMessageService;
import com.hpe.iot.dc.trinetra.model.Notification;
import com.hpe.iot.dc.udp.southbound.service.impl.UDPDatagramSender;

/**
 * @author sveera
 *
 */
@Component
public class StandardMessageService extends TrinetraAcknowledgementMessageService
		implements ExtendedUplinkMessageService {
	private static final String CONTAINER_NAME = "notification";
	private IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;

	@Autowired
	public StandardMessageService(UDPDatagramSender udpDatagramSender, DeviceDaoRepository deviceDaoRepository,
			IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService) {
		super(udpDatagramSender, deviceDaoRepository);
		this.iotPublisherService = iotPublisherService;
	}

	private static final List<String> SUPPORTED_MESSAGE_TYPES = Arrays.<String>asList("$", "S", "D", "s", "H", "G", "F",
			"_", "I", "i", "P", "L", "Z", "C", "b", "B", "a", "E", "m", "f", "e", "l", "y");

	public StandardMessageService(UDPDatagramSender udpDatagramSender, DeviceDaoRepository deviceDaoRepository) {
		super(udpDatagramSender, deviceDaoRepository);
	}

	@Override
	public List<String> getMessageTypes() {
		return SUPPORTED_MESSAGE_TYPES;
	}

	@Override
	public String getMessageType() {
		return "S";
	}

	@Override
	protected DeviceDataDeliveryStatus executeMessageSpecificLogic(DeviceInfo model) {
		Map<String, DeviceData> devicedata = model.getDeviceData();
		DeviceData deviceData = devicedata.get(Notification.NOTIF_MESS_TYP);
		if (deviceData != null && deviceData instanceof Notification) {
			Notification notification = (Notification) deviceData;
			if (!notification.getNotifications().isEmpty())
				iotPublisherService.receiveDataFromDevice(model, CONTAINER_NAME);
		}
		return new DeviceDataDeliveryStatus();
	}
}
