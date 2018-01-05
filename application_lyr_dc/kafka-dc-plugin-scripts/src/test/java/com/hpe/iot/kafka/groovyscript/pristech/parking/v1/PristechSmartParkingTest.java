/**
 * 
 */
package com.hpe.iot.kafka.groovyscript.pristech.parking.v1;

import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH;
import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH_MODEL;
import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.kafka.test.base.KafkaDCPluginScriptTestBaseTemplate;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class PristechSmartParkingTest extends KafkaDCPluginScriptTestBaseTemplate {

	private static final String DEVICE_ID = "3c003434";

	@Test
	public void testPristechSmartParkingUplinkParkingEvent() throws InterruptedException {
		kafkaDevicePublisherService.publishData(formUplinkTopicName(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION),
				getPristechSmartParkingUplinkParkingEventMsg());
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		validateDeviceData(deviceInfo);
		assertEquals("PARKING_EVENT", deviceInfo.getMessageType(), "Expected and actual Message Type are not same");
	}

	@Test
	public void testPristechSmartParkingUplinkHealthCheck() throws InterruptedException {
		kafkaDevicePublisherService.publishData(formUplinkTopicName(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION),
				getPristechSmartParkingUplinkHealthCheckMsg());
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		validateDeviceData(deviceInfo);
		assertEquals("PARKING_HEALTH", deviceInfo.getMessageType(), "Expected and actual Message Type are not same");

	}

	private String getPristechSmartParkingUplinkParkingEventMsg() {
		return "{\"event_id\": 0, \"event_str\": \"PARKING_EVENT\", \"sensor_id\": \"3c003434\", \"sensor_str\": \"PPARKE_GRND_BLR_01\", "
				+ "\"sensor_type\": \"MAG_IR\", \"base_station_str\":\"PPARKE_BLR_01\", \"base_station_id\":\"094ed301\","
				+ " \"timestamp\": \"2017-02-21T08:27:14.485Z\", \"location\": { \"longitude\": 77.6113940000000040, \"latitude\": 12.9344900000000000 },"
				+ " \"parked\": 1 }";
	}

	private String getPristechSmartParkingUplinkHealthCheckMsg() {
		return "{\"event_id\": 1, \"event_str\": \"PARKING_HEALTH\", \"sensor_id\": \"3c003434\", \"sensor_str\": \"PPARKE_GRND_BLR_01\", "
				+ "\"sensor_type\": \"MAG_IR\", \"base_station_str\":\"PPARKE_BLR_01\", \"base_station_id\":\"094ed301\","
				+ " \"timestamp\": \"2017-02-21T08:45:00.043Z\", \"last_timestamp\": \"2017-02-21T08:30:00.043Z\","
				+ " \"location\": { \"longitude\": 77.6113940000000040, \"latitude\": 12.9344900000000000 }, \"current_status\": 1, \"last_tx_status\": 0}";
	}

	private void validateDeviceData(DeviceInfo deviceInfo) {
		assertNotNull(deviceInfo, "DeviceInfo object cannot be null");
		Device device = deviceInfo.getDevice();
		assertNotNull(device, "Device object cannot be null");
		assertEquals(PRISTECH, device.getManufacturer(), "Expected and actual Manufacturer are not same");
		assertEquals(PRISTECH_MODEL, device.getModelId(), "Expected and actual Model are not same");
		assertEquals(PRISTECH_VERSION, device.getVersion(), "Expected and actual Version are not same");
		assertEquals(DEVICE_ID, device.getDeviceId(), "Expected and actual DeviceId are not same");
	}

}
