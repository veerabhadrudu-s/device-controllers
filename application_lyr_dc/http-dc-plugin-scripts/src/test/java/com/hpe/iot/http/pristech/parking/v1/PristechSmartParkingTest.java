/**
 * 
 */
package com.hpe.iot.http.pristech.parking.v1;

import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH;
import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class PristechSmartParkingTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "3c003434";

	@Test
	@DisplayName("test Process DevicePayload For Pristech Smart Parking Uplink Parking Event")
	public void testPristechSmartParkingUplinkParkingEvent() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION,
				getPristechSmartParkingUplinkParkingEventMsg());
		validateConsumedPristechUplinkMessage("PARKING_EVENT", iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	@Test
	@DisplayName("test Process DevicePayload For Pristech Smart Parking Uplink Health Check")
	public void testPristechSmartParkingUplinkHealthCheck() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION,
				getPristechSmartParkingUplinkHealthCheckMsg());
		validateConsumedPristechUplinkMessage("PARKING_HEALTH", iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	@Test
	@Disabled
	@DisplayName("test Process DevicePayload For Pristech Smart Parking Downlink Command")
	public void testPristechSmartParkingDownlinkCommandForPpark() throws Exception {
		mockNorthboundDownlinkProducerService.publishDownlinkData(createNorthboundOneM2MDownlinkCommandForPPark());
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

	private void validateConsumedPristechUplinkMessage(String expectedMessageType, DeviceInfo deviceInfo)
			throws InterruptedException {
		validateConsumedUplinkMessage(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION, DEVICE_ID, deviceInfo);
		assertEquals(expectedMessageType, deviceInfo.getMessageType(), "Expected and actual Message Type are not same");
	}

	private String createNorthboundOneM2MDownlinkCommandForPPark() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<?xml-oneM2m oneM2M=\"1.10\"?><ns2:requestPrimitive xmlns:ns2=\"http://www.onem2m.org/xml/protocols\">"
				+ "<operation>5</operation><to>HPE_IoT/1266365f32343839</to><from>/CSE1000</from>"
				+ "<requestIdentifier>702b3f031d9e4c6bb159823560bd3de6</requestIdentifier>"
				+ "<primitiveContent><ns2:notification><notificationEvent>"
				+ "<representation xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "{\"m2m:cin\":{\"ty\":4,\"ri\":\"HPE_IoT/1266365f32343839/downlinkCommand/da94f591ac1\",\"pi\":\"HPE_IoT/1266365f32343839/downlinkCommand\","
				+ "\"ct\":\"20171120T093605,000187\",\"lt\":\"20171120T093605,000187\",\"rn\":\"da94f591ac1\",\"et\":\"20271118T093605,000178\","
				+ "\"st\":1,\"cr\":\"C0D9240EE-87a194ed\",\"cnf\":\"text/plain:0\",\"cs\":810,"
				+ "\"con\":\"{\\\"device\\\":{\\\"manufacturer\\\":\\\"pristech\\\",\\\"modelId\\\":\\\"smartparking\\\",\\\"version\\\":\\\"1.0\\\",\\\"deviceId\\\":\\\"1266365f32343839\\\"},"
				+ "\\\"messageType\\\":\\\"downlinkCommand\\\",\\\"payload\\\":{\\\"event_str\\\":\\\"CONFIG_BS\\\",\\\"event_id\\\":1,\\\"sensor_id\\\":\\\"3c003434\\\",\\\"sensor_str\\\":\\\"PPARKE_GRND_BLR_01\\\",\\\"base_station_str\\\":\\\"PPARKE_BLR_01\\\","
				+ "\\\"base_station_id\\\":\\\"094ed301\\\",\\\"timestamp\\\":\\\"2017-02-21T08:27:14.485Z\\\",\\\"location\\\":{\\\"longitude\\\":77.611394,\\\"latitude\\\":12.93449}}}\"}}"
				+ "</representation><operationMonitor><operation>5</operation><originator>C0D9240EE-643af5f2</originator></operationMonitor>"
				+ "<notificationEventType>1</notificationEventType></notificationEvent><verificationRequest>false</verificationRequest><subscriptionReference>5345597890245247980</subscriptionReference><creator>C0D9240EE-643af5f2</creator>"
				+ "</ns2:notification></primitiveContent><responseType><responseTypeValue>2</responseTypeValue></responseType></ns2:requestPrimitive>";
	}

}
