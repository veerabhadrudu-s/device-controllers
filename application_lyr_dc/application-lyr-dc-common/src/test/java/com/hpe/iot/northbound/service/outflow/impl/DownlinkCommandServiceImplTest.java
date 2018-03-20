package com.hpe.iot.northbound.service.outflow.impl;

import static com.hpe.iot.test.constants.TestConstants.EMPTY_DEVICE_MODELS_FULL_PATH;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.factory.BrokerConsumerServiceFactory;
import com.hpe.broker.service.consumer.factory.impl.BrokerConsumerServiceFactoryImpl;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.impl.GroovyAndUplinkJsonPathDeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandServiceHandler;
import com.hpe.iot.northbound.service.outflow.impl.DownlinkCommandServiceHandlerImpl.DeviceModelNotSuported;
import com.hpe.iot.northbound.service.outflow.impl.DownlinkCommandServiceHandlerImpl.DownlinkFlowNotSupported;

/**
 * @author sveera
 *
 */
public class DownlinkCommandServiceImplTest {

	private static final String SENSENUTS_VERSION = "1.0";
	private static final String SENSENUTS_MODEL = "lighting";
	private static final String SENSENUTS_MANUFAC = "sensenuts";
	private static final String DOWNLINK_CMD_PAYLOAD = "{\"gatewayId\":\"1234\",\"lightId\":\"0001\",\"brightness\":\"50\"}";
	private final MockBrokerConsumerService mockBrokerConsumerService = new MockBrokerConsumerService();
	private final PayloadCipherSpy payloadCipherSpy = new PayloadCipherSpy();
	private final DownlinkPayloadProcessorSpy downlinkPayloadProcessorSpy = new DownlinkPayloadProcessorSpy();
	private DownlinkCommandServiceImpl downlinkCommandServiceImpl;

	@BeforeEach
	public void setUp() throws Exception {
		List<BrokerConsumerService<String>> brokerConsumerServices = new ArrayList<>();
		brokerConsumerServices.add(mockBrokerConsumerService);
		BrokerConsumerServiceFactory<String> brokerConsumerServiceFactory = new BrokerConsumerServiceFactoryImpl<>(
				brokerConsumerServices);
		GroovyAndUplinkJsonPathDeviceModelFactory groovyAndUplinkJsonPathDeviceModelFactory = new GroovyAndUplinkJsonPathDeviceModelFactory(
				EMPTY_DEVICE_MODELS_FULL_PATH);
		groovyAndUplinkJsonPathDeviceModelFactory.addGroovyDeviceModel(SENSENUTS_MANUFAC, SENSENUTS_MODEL,
				SENSENUTS_VERSION, new GroovyScriptDeviceModel(SENSENUTS_MANUFAC, SENSENUTS_MODEL, SENSENUTS_VERSION));
		groovyAndUplinkJsonPathDeviceModelFactory.addGroovyDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION,
				new GroovyScriptDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION));
		NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory = new NorthboundPayloadExtractorFactory();
		northboundPayloadExtractorFactory.addPayloadCipher(SENSENUTS_MANUFAC, SENSENUTS_MODEL, SENSENUTS_VERSION,
				payloadCipherSpy);
		northboundPayloadExtractorFactory.addDownlinkPayloadProcessor(SENSENUTS_MANUFAC, SENSENUTS_MODEL,
				SENSENUTS_VERSION, downlinkPayloadProcessorSpy);
		DownlinkCommandServiceHandler downlinkCommandServiceHandler = new DownlinkCommandServiceHandlerImpl(
				groovyAndUplinkJsonPathDeviceModelFactory, northboundPayloadExtractorFactory);
		downlinkCommandServiceImpl = new DownlinkCommandServiceImpl(downlinkCommandServiceHandler,
				brokerConsumerServiceFactory, mockBrokerConsumerService.getName(), "downLinkDestination");
	}

	@Test
	@DisplayName("Is DownlinkCommandServiceImpl not null")
	public final void testDownlinkCommandServiceImpl() {
		assertNotNull(downlinkCommandServiceImpl, DownlinkCommandServiceImpl.class.getSimpleName() + " cannot be null");
	}

	@Test
	@DisplayName("Is Downlink command processed")
	public final void testDownlinkCommandPayloadProcessing() {
		JsonParser jsonParser = new JsonParser();
		JsonObject expectedDownlinkPayload = jsonParser.parse(DOWNLINK_CMD_PAYLOAD).getAsJsonObject();
		downlinkCommandServiceImpl.startService();
		mockBrokerConsumerService
				.publishDownlinkCommand(createNorthboundOneM2MDownlinkCommandForSensenutsChangeBrightness());
		assertNotNull(payloadCipherSpy.getPayload(), "Downlink payload cannot be null");
		assertNotNull(downlinkPayloadProcessorSpy.getPayload(), "Downlink payload cannot be null");
		assertEquals(expectedDownlinkPayload, payloadCipherSpy.getPayload(),
				"Expected and actual downlink payloads are not equal");
		assertEquals(expectedDownlinkPayload, downlinkPayloadProcessorSpy.getPayload(),
				"Expected and actual downlink payloads are not equal");
	}

	@Test
	@DisplayName("test Downlink Command Payload Processing For DeviceModelNotSuportedException")
	public final void testDownlinkCommandPayloadProcessingForDeviceModelNotSuportedException() {
		downlinkCommandServiceImpl.startService();
		assertThrows(DeviceModelNotSuported.class, () -> mockBrokerConsumerService
				.publishDownlinkCommand(createNorthboundOneM2MDownlinkCommandForUnsupportedDeviceModel()));
	}

	@Test
	@DisplayName("test Downlink Command Payload Processing For DownlinkFlowNotSupported")
	public final void testDownlinkCommandPayloadProcessingForDownlinkFlowNotSupported() {
		downlinkCommandServiceImpl.startService();
		assertThrows(DownlinkFlowNotSupported.class, () -> mockBrokerConsumerService
				.publishDownlinkCommand(createNorthboundOneM2MDownlinkCommandForDownlinkFlowNotSupportedModel()));
	}

	private class MockBrokerConsumerService implements BrokerConsumerService<String> {

		private BrokerConsumerDataHandler<String> brokerConsumerDataHandler;
		private String destination;

		@Override
		public String getName() {
			return "mockConsumerService";
		}

		@Override
		public void startService() {

		}

		@Override
		public void stopService() {

		}

		@Override
		public void consumeData(String destination, BrokerConsumerDataHandler<String> brokerConsumerDataHandler) {
			this.destination = destination;
			this.brokerConsumerDataHandler = brokerConsumerDataHandler;
		}

		public void publishDownlinkCommand(String downlinkComamnd) {
			this.brokerConsumerDataHandler.handleConsumerMessage(destination, downlinkComamnd);
		}

	}

	private String createNorthboundOneM2MDownlinkCommandForSensenutsChangeBrightness() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?xml-oneM2m oneM2M=\"1.10\"?><ns2:requestPrimitive xmlns:ns2=\"http://www.onem2m.org/xml/protocols\">"
				+ "<operation>5</operation><to>HPE_IoT/Light-1234</to><from>/CSE1000</from><requestIdentifier>45f440d7b169453f8e5f00e15046444b</requestIdentifier><primitiveContent><ns2:notification><notificationEvent><representation xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "{\"m2m:cin\":{\"ty\":4,\"ri\":\"HPE_IoT/Light-1234/downlinkCommand/8969087f43c\",\"pi\":\"HPE_IoT/Light-1234/downlinkCommand\",\"ct\":\"20171009T084338,000232\",\"lt\":\"20171009T084338,000232\",\"rn\":\"8969087f43c\",\"et\":\"20271007T084338,000220\",\"st\":1,\"cr\":\"C070878C6-85f18dc6\",\"cnf\":\"text/plain:0\",\"cs\":352,"
				+ "\"con\":\"{\\\"device\\\":{\\\"manufacturer\\\":\\\"sensenuts\\\",\\\"modelId\\\":\\\"lighting\\\",\\\"version\\\":\\\"1.0\\\",\\\"deviceId\\\":\\\"1234\\\"},\\\"messageType\\\":\\\"downlinkCommand\\\",\\\"payload\\\":"
				+ "{\\\"gatewayId\\\":\\\"1234\\\",\\\"lightId\\\":\\\"0001\\\",\\\"brightness\\\":\\\"50\\\"}}\"}}</representation><operationMonitor>"
				+ "<operation>5</operation><originator>C070878C6-c4c0c7c1</originator></operationMonitor><notificationEventType>1</notificationEventType></notificationEvent><verificationRequest>false</verificationRequest><subscriptionReference>7524845195623549720</subscriptionReference><creator>C070878C6-c4c0c7c1</creator></ns2:notification>"
				+ "</primitiveContent><responseType><responseTypeValue>2</responseTypeValue></responseType></ns2:requestPrimitive>";
	}

	private String createNorthboundOneM2MDownlinkCommandForUnsupportedDeviceModel() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?xml-oneM2m oneM2M=\"1.10\"?><ns2:requestPrimitive xmlns:ns2=\"http://www.onem2m.org/xml/protocols\">"
				+ "<operation>5</operation><to>HPE_IoT/Light-1234</to><from>/CSE1000</from><requestIdentifier>45f440d7b169453f8e5f00e15046444b</requestIdentifier><primitiveContent><ns2:notification><notificationEvent><representation xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "{\"m2m:cin\":{\"ty\":4,\"ri\":\"HPE_IoT/Light-1234/downlinkCommand/8969087f43c\",\"pi\":\"HPE_IoT/Light-1234/downlinkCommand\",\"ct\":\"20171009T084338,000232\",\"lt\":\"20171009T084338,000232\",\"rn\":\"8969087f43c\",\"et\":\"20271007T084338,000220\",\"st\":1,\"cr\":\"C070878C6-85f18dc6\",\"cnf\":\"text/plain:0\",\"cs\":352,"
				+ "\"con\":\"{\\\"device\\\":{\\\"manufacturer\\\":\\\"invalid\\\",\\\"modelId\\\":\\\"invalid_model\\\",\\\"version\\\":\\\"1.0\\\",\\\"deviceId\\\":\\\"1234\\\"},\\\"messageType\\\":\\\"downlinkCommand\\\",\\\"payload\\\":"
				+ "{\\\"gatewayId\\\":\\\"1234\\\",\\\"lightId\\\":\\\"0001\\\",\\\"brightness\\\":\\\"50\\\"}}\"}}</representation><operationMonitor>"
				+ "<operation>5</operation><originator>C070878C6-c4c0c7c1</originator></operationMonitor><notificationEventType>1</notificationEventType></notificationEvent><verificationRequest>false</verificationRequest><subscriptionReference>7524845195623549720</subscriptionReference><creator>C070878C6-c4c0c7c1</creator></ns2:notification>"
				+ "</primitiveContent><responseType><responseTypeValue>2</responseTypeValue></responseType></ns2:requestPrimitive>";
	}

	private String createNorthboundOneM2MDownlinkCommandForDownlinkFlowNotSupportedModel() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?xml-oneM2m oneM2M=\"1.10\"?><ns2:requestPrimitive xmlns:ns2=\"http://www.onem2m.org/xml/protocols\">"
				+ "<operation>5</operation><to>HPE_IoT/Light-1234</to><from>/CSE1000</from><requestIdentifier>45f440d7b169453f8e5f00e15046444b</requestIdentifier><primitiveContent><ns2:notification><notificationEvent><representation xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "{\"m2m:cin\":{\"ty\":4,\"ri\":\"HPE_IoT/Light-1234/downlinkCommand/8969087f43c\",\"pi\":\"HPE_IoT/Light-1234/downlinkCommand\",\"ct\":\"20171009T084338,000232\",\"lt\":\"20171009T084338,000232\",\"rn\":\"8969087f43c\",\"et\":\"20271007T084338,000220\",\"st\":1,\"cr\":\"C070878C6-85f18dc6\",\"cnf\":\"text/plain:0\",\"cs\":352,"
				+ "\"con\":\"{\\\"device\\\":{\\\"manufacturer\\\":\\\"" + SAMPLE + "\\\",\\\"modelId\\\":\\\""
				+ SAMPLE_MODEL + "\\\",\\\"version\\\":\\\"" + SAMPLE_VERSION
				+ "\\\",\\\"deviceId\\\":\\\"1234\\\"},\\\"messageType\\\":\\\"downlinkCommand\\\",\\\"payload\\\":"
				+ "{\\\"gatewayId\\\":\\\"1234\\\",\\\"lightId\\\":\\\"0001\\\",\\\"brightness\\\":\\\"50\\\"}}\"}}</representation><operationMonitor>"
				+ "<operation>5</operation><originator>C070878C6-c4c0c7c1</originator></operationMonitor><notificationEventType>1</notificationEventType></notificationEvent><verificationRequest>false</verificationRequest><subscriptionReference>7524845195623549720</subscriptionReference><creator>C070878C6-c4c0c7c1</creator></ns2:notification>"
				+ "</primitiveContent><responseType><responseTypeValue>2</responseTypeValue></responseType></ns2:requestPrimitive>";
	}

	private class PayloadCipherSpy implements PayloadCipher {

		private JsonObject payload;

		@Override
		public JsonObject cipherPayload(DeviceModel deviceModel, JsonObject payload) {
			this.payload = payload;
			return payload;
		}

		public JsonObject getPayload() {
			return payload;
		}

	}

	private class DownlinkPayloadProcessorSpy implements DownlinkPayloadProcessor {

		private JsonObject payload;

		@Override
		public void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload) {
			this.payload = decipheredPayload.getPayload();
		}

		public JsonObject getPayload() {
			return payload;
		}

	}

}
