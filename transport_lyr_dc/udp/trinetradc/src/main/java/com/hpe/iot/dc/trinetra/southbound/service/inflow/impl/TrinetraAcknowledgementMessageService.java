package com.hpe.iot.dc.trinetra.southbound.service.inflow.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.dao.DeviceDaoRepository;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.trinetra.model.DataModelConstants;
import com.hpe.iot.dc.udp.southbound.service.impl.UDPDatagramSender;
import com.handson.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public abstract class TrinetraAcknowledgementMessageService implements MessageService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected UDPDatagramSender udpDatagramSender;

	protected DeviceDaoRepository deviceDaoRepository;

	public TrinetraAcknowledgementMessageService(UDPDatagramSender udpDatagramSender,
			DeviceDaoRepository deviceDaoRepository) {
		super();
		this.udpDatagramSender = udpDatagramSender;
		this.deviceDaoRepository = deviceDaoRepository;
	}

	@Override
	public DeviceDataDeliveryStatus executeService(DeviceInfo model) {
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = executeMessageSpecificLogic(model);
		sendAcknowledgementToDevice(model.getDevice());
		return deviceDataDeliveryStatus;
	}

	protected abstract DeviceDataDeliveryStatus executeMessageSpecificLogic(DeviceInfo model);

	private void sendAcknowledgementToDevice(Device device) {
		Map<String, String> deviceInformation = deviceDaoRepository.readDeviceInformation(device);
		String host = deviceInformation.get(DataModelConstants.SOURCE_IP);
		int port = Integer.parseInt(deviceInformation.get(DataModelConstants.SOURCE_PORT));
		byte[] message = createAcknowledgment();
		logger.info("Raw data to output channel in hexa format is ");
		UtilityLogger.logRawDataInHexaDecimalFormat(message, getClass());
		udpDatagramSender.sendUDPDatagram(host, port, message);

	}

	private byte[] createAcknowledgment() {
		int checksum = 0;
		checksum ^= 5;
		checksum ^= 65;
		checksum ^= 65;
		checksum = checksum == 0 || checksum == 60 || checksum == 62 ? 64 : checksum;
		final String acknowledgement = "<" + new String(new byte[] { 5 }) + "A"
				+ new String(new byte[] { (byte) (checksum) }) + ">";
		logger.debug("Acknowledgment data is " + acknowledgement);
		logger.debug("Acknowledgment data length " + acknowledgement.length());
		logger.debug("Acknowledgment Checksum value " + checksum);
		return acknowledgement.getBytes();

	}

}
