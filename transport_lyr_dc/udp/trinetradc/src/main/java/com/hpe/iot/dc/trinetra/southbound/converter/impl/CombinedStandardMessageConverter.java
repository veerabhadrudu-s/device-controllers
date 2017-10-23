/**
 * 
 */
package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.model.DeviceData;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.trinetra.model.Notification;
import com.hpe.iot.dc.trinetra.model.NotificationRecord;
import com.hpe.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
@Component
public class CombinedStandardMessageConverter implements UplinkDeviceDataConverter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final StandardMessageCreator standardMessageCreator;

	@Autowired
	public CombinedStandardMessageConverter(StandardMessageCreator standardMessageCreator) {
		super();
		this.standardMessageCreator = standardMessageCreator;
	}

	@Override
	public String getMessageType() {
		return "y";
	}

	@Override
	public DeviceInfo createModel(DeviceModel deviceModel, byte[] input) {
		String deviceId = DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(input[3], input[2], input[1]);
		DeviceInfo deviceInfo = new DeviceInfo(
				new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), "1.0", deviceId),
				getMessageType(), input);
		addDeviceData(deviceInfo.getDeviceData(), input);
		return deviceInfo;
	}

	private void addDeviceData(final Map<String, DeviceData> deviceData, byte[] input) {
		List<NotificationRecord> notifications = new ArrayList<>();
		int noOfRecords = input[5];
		int eachRecordLength = input[6];
		byte[] allRecordsData = Arrays.copyOfRange(input, 7, input.length - 2);
		byte[][] splittedRecords = DataParserUtility.splitArray(allRecordsData, eachRecordLength);
		logger.debug("Total Number of Records found in bulk notification " + noOfRecords);
		logger.debug("Each record length is " + eachRecordLength);
		logger.trace("Length of All Records data is " + allRecordsData.length);
		Notification notification = new Notification(noOfRecords, notifications);
		for (byte[] eachRecord : splittedRecords) {
			NotificationRecord notificationRecord = standardMessageCreator
					.constructStandardMessage(DataParserUtility.convertBytesToASCIIString(input, 0, 1), eachRecord);
			if (notificationRecord != null)
				notifications.add(notificationRecord);
		}

		deviceData.put(notification.getDeviceDataInformation(), notification);
	}

}
