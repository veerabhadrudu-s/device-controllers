package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.TrackerNotification.TRACKER_NOTIF
import static com.hpe.iot.dc.mmi.safemate.TrackerNotification.NotificationType.IPCONNECT
import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.OFF;
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.IPCONNECT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload

import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.mmi.safemate.GPSInfo
import com.hpe.iot.dc.mmi.safemate.IPConnectMessageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerNotification
import com.hpe.iot.dc.mmi.safemate.TrackerStatus
import com.hpe.iot.dc.model.DeviceInfo;

import static org.junit.Assert.assertEquals;

/**
 * @author sveera
 *
 */
public class IPConnectMessageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4001";

	private IPConnectMessageConverter ipAddressMetaModelConverter;

	@Before
	public void setUpBeforeClass() throws Exception {
		ipAddressMetaModelConverter = new IPConnectMessageConverter(new MMICRCAlgorithm(), new TrackerInfoCreator());
	}

	@Test
	public void testGetOperator() {
		assertEquals("Expected and actual Operators are not same", EXPECTED_MESSAGE_TYPE,
				ipAddressMetaModelConverter.getMessageType());
	}

	@Test
	public void testCreateModeForIPConnectMessageType() {
		DeviceInfo dataModel = ipAddressMetaModelConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(IPCONNECT_DATA_MESSAGE_HEX, this.getClass()));
		assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", MANUFACTURER,
				dataModel.getDevice().getManufacturer());
		assertEquals("Expected Model ID and Actual Model ID are not Same", MODEL_ID,
				dataModel.getDevice().getModelId());
		assertEquals("Expected and actual device Id's are not same", "301071500007", dataModel.getDevice().getDeviceId());
		TrackerNotification trackerNotification = createExpectedData();
		assertEquals("Expected and actual device data are not same", trackerNotification,
				dataModel.getDeviceData().get(TRACKER_NOTIF));
		assertEquals("Expected and actual device data are not same", EXPECTED_MESSAGE_TYPE, dataModel.getMessageType());
	}

	private TrackerNotification createExpectedData() {
		List<TrackerInfo> trackerInfos=new ArrayList<>();
		trackerInfos.add(new TrackerInfo(new GPSInfo("12-2-2001", "12:21:23 GMT", 4228040996/3600000, 1092906/3600000, 377.82, 768), 100,
				new TrackerStatus(OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF)));
		return new TrackerNotification(IPCONNECT, 1, trackerInfos);
	}
}
