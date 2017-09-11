package com.hpe.iot.dc.trinetra.southbound.service.inflow.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.dao.DeviceDaoRepository;
import com.hpe.iot.dc.model.DeviceData;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.model.DataModelConstants;
import com.hpe.iot.dc.trinetra.model.DeviceAddress;
import com.hpe.iot.dc.udp.southbound.service.impl.UDPDatagramSender;

/**
 * @author sveera
 *
 */
@Component
public class IpAddressMessageService extends TrinetraAcknowledgementMessageService {

	private static final Logger logger = LoggerFactory.getLogger(IpAddressMessageService.class);

	@Autowired
	public IpAddressMessageService(UDPDatagramSender udpDatagramSender, DeviceDaoRepository deviceDaoRepository) {
		super(udpDatagramSender, deviceDaoRepository);
	}

	public String getMessageType() {
		return "#";
	}

	@Override
	protected DeviceDataDeliveryStatus executeMessageSpecificLogic(DeviceInfo model) {
		logger.info("Received Data model for Ip Update Message processing is " + model);
		Map<String, DeviceData> metaInforamtion = model.getDeviceData();
		DeviceAddress deviceAddress = (DeviceAddress) metaInforamtion.get(DeviceAddress.DEVICE_ADDRESS);
		Map<String, String> deviceInforamtion = new HashMap<String, String>();
		deviceInforamtion.put(DataModelConstants.SOURCE_IP, deviceAddress.getDeviceIp());
		deviceInforamtion.put(DataModelConstants.SOURCE_PORT, deviceAddress.getPort());
		deviceDaoRepository.storeDeviceInformation(model.getDevice(), deviceInforamtion);
		return new DeviceDataDeliveryStatus();
	}

}
