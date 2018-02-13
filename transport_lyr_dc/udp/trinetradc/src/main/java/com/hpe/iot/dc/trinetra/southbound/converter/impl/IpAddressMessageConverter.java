package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.trinetra.model.DeviceAddress;
import com.hpe.iot.dc.udp.model.UDPDevice;
import com.handson.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
@Component
public class IpAddressMessageConverter implements UplinkDeviceDataConverter {

	private static final Logger logger = LoggerFactory.getLogger(IpAddressMessageConverter.class);

	public String getMessageType() {
		return "#";
	}

	public DeviceInfo createModel(DeviceModel deviceModel, byte[] input) {
		final String deviceId = Long.toString(Long.parseLong(DataParserUtility.convertBytesToASCIIString(input, 1, 6)));
		logger.debug("Identified Device Id is " + deviceId);
		final DeviceInfo dataModel = new DeviceInfo(
				new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), "1.0", deviceId),
				getMessageType(), input);
		addMetaInformationForOperation(deviceModel, dataModel, input);
		return dataModel;
	}

	private void addMetaInformationForOperation(DeviceModel deviceModel, DeviceInfo dataModel, byte[] input) {
		DeviceAddress deviceIpInfo = deviceModel instanceof UDPDevice
				? getDeviceAddressFromUDPDevice((UDPDevice) deviceModel)
				: getDeviceAddressFromPayload(input);
		dataModel.addDeviceData(deviceIpInfo.getDeviceDataInformation(), deviceIpInfo);
	}

	private DeviceAddress getDeviceAddressFromUDPDevice(UDPDevice udpDevice) {
		return new DeviceAddress(udpDevice.getAddress().getHostAddress(), Integer.toString(udpDevice.getPort()));
	}

	private DeviceAddress getDeviceAddressFromPayload(byte[] input) {
		String deviceIp = decodeSourceIp(input);
		String port = decodeSourcePort(input);
		return new DeviceAddress(deviceIp, port);
	}

	private String decodeSourceIp(byte[] input) {
		String sourceIp = "";
		for (int counter = 8; counter < 16; counter += 2)
			sourceIp += DataParserUtility.convertHexaValToDecimalVal(input[counter], input[counter + 1]) + ".";
		return sourceIp.substring(0, sourceIp.length() - 1);
	}

	private String decodeSourcePort(byte[] input) {
		String port = "" + DataParserUtility.convertHexaValToDecimalVal(input[16], input[17], input[18], input[19]);
		return port;
	}

}
