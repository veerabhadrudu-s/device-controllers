/**
 * 
 */
package com.hpe.iot.southbound.service.inflow.impl;

import static com.hpe.iot.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_VERSION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.broker.service.activemq.EmbeddedActivemqBroker;
import com.hpe.broker.service.producer.BrokerProducerService;
import com.hpe.broker.service.producer.activemq.ActiveMQProducerService;
import com.hpe.broker.service.producer.factory.impl.BrokerProducerServiceFactoryImpl;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.model.factory.impl.UplinkJsonPathDeviceModelFactory;
import com.hpe.iot.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.northbound.service.inflow.impl.IOTPublisherHandler;
import com.hpe.iot.northbound.service.inflow.impl.IOTPublisherServiceImpl;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultPayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultUplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.impl.JsonPathDeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.impl.JsonPathMessageTypeExtractor;
import com.hpe.iot.southbound.service.inflow.SouthboundService;
import com.hpe.iot.test.constants.TestConstants;

/**
 * @author sveera
 *
 */
public class SouthboundServiceImplTest {

	private final String brokerURL = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000";
	private final String embeddedBrokerUrl = "tcp://localhost:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;wireFormat.maxInactivityDuration=120000";
	private EmbeddedActivemqBroker embeddedActivemqBroker;
	private BrokerProducerServiceFactoryImpl<String> brokerProducerServiceFactory;
	private IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;
	private UplinkPayloadProcessor uplinkPayloadProcessor;
	private final DeviceModelFactory deviceMetaModelFactory = new UplinkJsonPathDeviceModelFactory(
			TestConstants.DEVICE_MODELS_FULL_PATH);
	private final SouthboundPayloadExtractorFactory payloadExtractorFactory = new SouthboundPayloadExtractorFactory();
	private final JsonPathDeviceIdExtractor deviceIdExtractor = new JsonPathDeviceIdExtractor();
	private final MessageTypeExtractor messageTypeExtractor = new JsonPathMessageTypeExtractor();
	private final PayloadDecipher payloadDecipher = new DefaultPayloadDecipher();

	private SouthboundService southboundService;

	@BeforeEach
	public void setUp() throws Exception {
		embeddedActivemqBroker = new EmbeddedActivemqBroker(embeddedBrokerUrl);
		List<BrokerProducerService<String>> brokerProducerServices = new ArrayList<>();
		brokerProducerServiceFactory = new BrokerProducerServiceFactoryImpl<String>(brokerProducerServices);
		iotPublisherService = new IOTPublisherServiceImpl(
				new IOTPublisherHandler(brokerProducerServiceFactory, "uplinkDestination", "activemq"),
				new DefaultIOTModelConverterImpl());
		uplinkPayloadProcessor = new DefaultUplinkPayloadProcessor(iotPublisherService);
		brokerProducerServices.add(new ActiveMQProducerService(brokerURL));
		payloadExtractorFactory.addDeviceIdExtractor(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, deviceIdExtractor);
		payloadExtractorFactory.addDeviceIdExtractor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, deviceIdExtractor);
		payloadExtractorFactory.addDeviceIdExtractor(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION, deviceIdExtractor);
		payloadExtractorFactory.addMessageTypeExtractor(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION,
				messageTypeExtractor);
		payloadExtractorFactory.addMessageTypeExtractor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, messageTypeExtractor);
		payloadExtractorFactory.addMessageTypeExtractor(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION,
				messageTypeExtractor);
		payloadExtractorFactory.addPayloadDecipher(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, payloadDecipher);
		payloadExtractorFactory.addPayloadDecipher(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, payloadDecipher);
		payloadExtractorFactory.addPayloadDecipher(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION, payloadDecipher);
		payloadExtractorFactory.addUplinkPayloadProcessor(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION,
				uplinkPayloadProcessor);
		payloadExtractorFactory.addUplinkPayloadProcessor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, uplinkPayloadProcessor);
		payloadExtractorFactory.addUplinkPayloadProcessor(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION,
				uplinkPayloadProcessor);
		southboundService = new SouthboundServiceImpl(deviceMetaModelFactory, payloadExtractorFactory);
		embeddedActivemqBroker.startService();
	}

	@AfterEach
	public void tearDown() {
		for (Map.Entry<String, BrokerProducerService<String>> brokerProducerService : brokerProducerServiceFactory
				.getBrokerProducerServices().entrySet())
			brokerProducerService.getValue().stopService();
		embeddedActivemqBroker.stopService();
	}

	@Test
	public void testProcessDevicePayloadForInvalidDeviceModel() {
		Assertions.assertThrows(SouthboundServiceImpl.DeviceModelNotSuported.class, () -> {
			southboundService.processPayload("TestManufacturer", "TestModel", "1.0", new byte[] {});
		});
	}

	@Test
	public void testSouthboundService() {
		Assertions.assertNotNull(southboundService, "SouthboundService cannot be null");
	}

	@Test
	public void testProcessDevicePayloadForTrackimoMovingNotification() {
		JsonObject payload = createPayloadForTrackimoMovingNotification();
		southboundService.processPayload(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, payload.toString().getBytes());
	}

	@Test
	public void testProcessDevicePayloadForTrackimoSpeedNotification() {
		JsonObject payload = createPayloadForTrackimoSpeedNotification();
		southboundService.processPayload(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, payload.toString().getBytes());
	}

	@Test
	public void testProcessDevicePayloadForTrackimoFenceNotification() {
		JsonObject payload = createPayloadForTrackimoFenceNotification();
		southboundService.processPayload(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, payload.toString().getBytes());
	}

	@Test
	public void testProcessDevicePayloadForSampleNotification() {
		JsonObject payload = createPayloadForSampleNotification();
		southboundService.processPayload(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, payload.toString().getBytes());
	}

	@Test
	public void testProcessDevicePayloadForRexaWareBikeNotification() {
		JsonObject payload = createPayloadForRexaWareBikeNotification();
		southboundService.processPayload(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION, payload.toString().getBytes());
	}

	private JsonObject createPayloadForTrackimoMovingNotification() {
		JsonObject payload = new JsonObject();
		payload.addProperty("alarm_type", "Moving");
		payload.addProperty("device_id", "1129388");
		payload.addProperty("timestamp", "1462191410312");
		payload.addProperty("address",
				"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India");
		payload.addProperty("lat", "12.99726");
		payload.addProperty("lng", "77.690885");
		payload.addProperty("speed", "0");
		JsonObject extraPayload = new JsonObject();
		extraPayload.addProperty("string", "device 1129388 started moving");
		payload.add("extras", extraPayload);
		return payload;
	}

	private JsonObject createPayloadForTrackimoSpeedNotification() {
		JsonObject payload = new JsonObject();
		payload.addProperty("alarm_type", "Speed");
		payload.addProperty("device_id", "1129388");
		payload.addProperty("timestamp", "1462192085226");
		payload.addProperty("address",
				"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India");
		payload.addProperty("lat", "12.99726");
		payload.addProperty("lng", "77.690885");
		payload.addProperty("speed", "11");
		JsonObject extraPayload = new JsonObject();
		extraPayload.addProperty("speed", "11");
		payload.add("extras", extraPayload);
		return payload;
	}

	private JsonObject createPayloadForTrackimoFenceNotification() {
		JsonObject payload = new JsonObject();
		payload.addProperty("alarm_type", "Fence");
		payload.addProperty("device_id", "1129388");
		payload.addProperty("timestamp", "1462193525734");
		payload.addProperty("address",
				"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India");
		payload.addProperty("lat", "12.99726");
		payload.addProperty("lng", "77.690885");
		payload.addProperty("speed", "6");
		JsonObject extraPayload = new JsonObject();
		extraPayload.addProperty("name", "Test fence");
		payload.add("extras", extraPayload);
		return payload;
	}

	private JsonObject createPayloadForSampleNotification() {
		JsonObject payload = new JsonObject();
		payload.addProperty("message_type", "Notification");
		payload.addProperty("sample_id", "Sample123354");
		payload.addProperty("timestamp", "1462193525734");
		payload.addProperty("address",
				"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India");
		payload.addProperty("lat", "12.99726");
		payload.addProperty("lng", "77.690885");
		payload.addProperty("speed", "6");
		JsonObject extraPayload = new JsonObject();
		extraPayload.addProperty("type", "Notification");
		payload.add("extras", extraPayload);
		return payload;
	}

	private JsonObject createPayloadForRexaWareBikeNotification() {
		String payloadString = "{\"DeviceData\":[{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\",\"lat\":\"18.27363\",\"lon\":\"78.217891\",\"speed\":\"99\",\"altitude\":\"123\",\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\",\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\",\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\",\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\",\"OBDIntakAirTemperature\":\"\",\"OBDAbsThrottlePosition\":\"\",\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"},{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\",\"lat\":\"18.27363\",\"lon\":\"78.217891\",\"speed\":\"99\",\"altitude\":\"123\",\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\",\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\",\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\",\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\",\"OBDIntakAirTemperature\":\"\",\"OBDAbsThrottlePosition\":\"\",\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"}]}";
		return (JsonObject) new JsonParser().parse(payloadString);

	}

}
