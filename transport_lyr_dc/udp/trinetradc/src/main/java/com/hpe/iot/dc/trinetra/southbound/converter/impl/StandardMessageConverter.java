package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.ExtendedUplinkDeviceDataConverter;
import com.hpe.iot.dc.trinetra.model.Notification;
import com.hpe.iot.dc.trinetra.model.NotificationRecord;
import com.hpe.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
@Component
public class StandardMessageConverter implements ExtendedUplinkDeviceDataConverter {

	private final StandardMessageCreator standardMessageCreator;

	@Autowired
	public StandardMessageConverter(StandardMessageCreator standardMessageCreator) {
		super();
		this.standardMessageCreator = standardMessageCreator;
	}

	private static final List<String> SUPPORTED_MESSAGE_TYPES = Arrays.<String> asList("$", "S", "D", "s", "H", "G",
			"F", "_", "I", "i", "P", "L", "Z", "C", "b", "B", "a", "E", "m", "f", "e", "l");

	@Override
	public List<String> getMessageTypes() {
		return SUPPORTED_MESSAGE_TYPES;
	}

	@Override
	public String getMessageType() {
		return "$";
	}

	@Override
	public DeviceInfo createModel(DeviceModel deviceModel, byte[] input) {
		String deviceIdString = DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(input[3], input[2],
				input[1]);
		String messageType = identifyMessageType(input);
		DeviceInfo dataModel = new DeviceInfo(
				new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceIdString), messageType,
				input);
		addMetaInformationForMessageTypes(dataModel, input, messageType);
		return dataModel;
	}

	private String identifyMessageType(byte[] input) {
		int messageTypeIndex = getMessageTypes().indexOf(DataParserUtility.convertBytesToASCIIString(input, 4, 1));
		return getMessageTypes().get(messageTypeIndex);
	}

	private void addMetaInformationForMessageTypes(DeviceInfo dataModel, byte[] input, String messageType) {
		List<NotificationRecord> notificationRecords = new ArrayList<>();
		Notification notification = new Notification(1, notificationRecords);
		NotificationRecord notificationRecord = standardMessageCreator
				.constructStandardMessage(Arrays.copyOfRange(input, 4, input.length - 2));
		notificationRecords.add(notificationRecord);
		dataModel.addDeviceData(notification.getDeviceDataInformation(), notification);
	}

}