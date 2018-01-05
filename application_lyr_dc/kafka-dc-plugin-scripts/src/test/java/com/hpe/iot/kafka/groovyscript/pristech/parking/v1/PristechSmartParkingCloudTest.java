/**
 * 
 */
package com.hpe.iot.kafka.groovyscript.pristech.parking.v1;

import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH;
import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH_MODEL;
import static com.hpe.iot.kafka.test.constants.TestConstants.PRISTECH_VERSION;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.hpe.broker.service.producer.kafka.KafkaProducerService;

/**
 * @author sveera
 *
 */
public class PristechSmartParkingCloudTest {

	private KafkaProducerService<String, byte[]> kafkaDevicePublisherService;

	@BeforeEach
	public void setUp() {
		kafkaDevicePublisherService = new KafkaProducerService<>("10.3.239.72:9092",
				"org.apache.kafka.common.serialization.StringSerializer",
				"org.apache.kafka.common.serialization.ByteArraySerializer", "kafka-device-producer");
		kafkaDevicePublisherService.startService();
	}

	@AfterEach
	public void tearDown() {
		kafkaDevicePublisherService.stopService();
	}

	@Test
	@Disabled
	public void testPristechSmartParkingUplinkParkingEvent() {
		// for (int i = 0; i < 10000; i++)
		kafkaDevicePublisherService.publishData(formUplinkTopicName(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION),
				getPristechSmartParkingUplinkParkingEventMsg().getBytes());
	}

	@Test
	@Disabled
	public void testPristechSmartParkingUplinkHealthCheck() {
		// for (int i = 0; i < 10000; i++)
		kafkaDevicePublisherService.publishData(formUplinkTopicName(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION),
				getPristechSmartParkingUplinkHealthCheckMsg().getBytes());
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

	protected String formUplinkTopicName(String manufacturer, String model, String version) {
		return formTopicName(manufacturer, model, version, "Up");

	}

	protected String formDownlinkTopicName(String manufacturer, String model, String version) {
		return formTopicName(manufacturer, model, version, "Down");

	}

	private String formTopicName(String manufacturer, String model, String version, String flow) {
		return manufacturer + "_" + model + "_" + version + "_" + flow;
	}
}
