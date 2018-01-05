/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.hpe.iot.mqtt.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SAMPLE_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;

/**
 * @author sveera
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public class MqttSouthboundServiceTest {
	private final MqttClientPersistence persistence = new MemoryPersistence();
	private MqttClient mqttClient;

	@Value("${mqtt.broker.url}")
	private String mqttServerUrl;

	@Autowired
	private DeviceModelFactory deviceModelFactory;
	@Autowired
	private SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	@Autowired
	private NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;

	@BeforeEach
	public void beforeTest() throws InterruptedException, MqttException {
		waitForDCInitialization();
		mqttClient = new MqttClient(mqttServerUrl, "testcase-client", persistence);
		mqttClient.connect();
	}

	@Test
	@Disabled
	// Below Unit test is failing to compile as Groovy classes are compiled after
	// java class are compiled.
	@DisplayName("test verify DC Sample Plugin Script With Script Class Types")
	public void verifyDCSamplePluginScriptWithScriptClassTypes() {

		/*DeviceModel actualDeviceModel = deviceModelFactory.findDeviceModel(SAMPLE, SAMPLE_MODEL);
		DeviceIdExtractor deviceIdExtractor = payloadExtractorFactory.getDeviceIdExtractor(SAMPLE, SAMPLE_MODEL);
		MessageTypeExtractor messageTypeExtractor = payloadExtractorFactory.getMessageTypeExtractor(SAMPLE,
				SAMPLE_MODEL);
		PayloadDecipher payloadDecipher = payloadExtractorFactory.getPayloadDecipher(SAMPLE, SAMPLE_MODEL);
		UplinkPayloadProcessor uplinkPayloadProcessor = payloadExtractorFactory.getUplinkPayloadProcessor(SAMPLE,
				SAMPLE_MODEL);
		Assert.assertEquals("Expected Device Model and Actual Device Models are not same",
				new GroovyScriptDeviceModel(TestConstants.SAMPLE, TestConstants.SAMPLE_MODEL), actualDeviceModel);
		Assert.assertTrue("Expected DeviceIdExtractor and DeviceIdExtractor are not same",
				deviceIdExtractor.getClass().getName().equals(SampleModelDeviceIdExtractor.class.getName()));
		Assert.assertTrue("Expected MessageTypeExtractor and MessageTypeExtractor are not same",
				messageTypeExtractor.getClass().getName().equals(SampleModelMessageTypeExtractor.class.getName()));
		Assert.assertTrue("Expected PayloadDecipher and PayloadDecipher are not same",
				payloadDecipher.getClass().getName().equals(SampleModelPayloadDecipher.class.getName()));
		Assert.assertTrue("Expected UplinkPayloadProcessor and UplinkPayloadProcessor are not same",
				uplinkPayloadProcessor.getClass().getName().equals(SampleModelPayloadProcessor.class.getName()));*/

	}

	@Test
	@DisplayName("test Verify DC Sample Plugin Script")
	public void verifyDCSamplePluginScript() {
		DeviceModel actualDeviceModel = deviceModelFactory.findDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
		DeviceIdExtractor deviceIdExtractor = southboundPayloadExtractorFactory.getDeviceIdExtractor(SAMPLE,
				SAMPLE_MODEL, SAMPLE_VERSION);
		MessageTypeExtractor messageTypeExtractor = southboundPayloadExtractorFactory.getMessageTypeExtractor(SAMPLE,
				SAMPLE_MODEL, SAMPLE_VERSION);
		PayloadDecipher payloadDecipher = southboundPayloadExtractorFactory.getPayloadDecipher(SAMPLE, SAMPLE_MODEL,
				SAMPLE_VERSION);
		UplinkPayloadProcessor uplinkPayloadProcessor = southboundPayloadExtractorFactory
				.getUplinkPayloadProcessor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
		PayloadCipher payloadCipher = northboundPayloadExtractorFactory.getPayloadCipher(SAMPLE, SAMPLE_MODEL,
				SAMPLE_VERSION);
		DownlinkPayloadProcessor downlinkPayloadProcessor = northboundPayloadExtractorFactory
				.getDownlinkPayloadProcessor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
		assertEquals(new GroovyScriptDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), actualDeviceModel,
				"Expected Device Model and Actual Device Models are not same");
		assertTrue(deviceIdExtractor instanceof DeviceIdExtractor,
				"Expected DeviceIdExtractor and DeviceIdExtractor are not same");
		assertTrue(messageTypeExtractor instanceof MessageTypeExtractor,
				"Expected MessageTypeExtractor and MessageTypeExtractor are not same");
		assertTrue(payloadDecipher instanceof PayloadDecipher,
				"Expected PayloadDecipher and PayloadDecipher are not same");
		assertTrue(uplinkPayloadProcessor instanceof UplinkPayloadProcessor,
				"Expected UplinkPayloadProcessor and UplinkPayloadProcessor are not same");
		assertTrue(payloadCipher instanceof PayloadCipher, "Expected PayloadCipher and PayloadCipher are not same");
		assertTrue(downlinkPayloadProcessor instanceof DownlinkPayloadProcessor,
				"Expected DownlinkPayloadProcessor and DownlinkPayloadProcessor are not same");

	}

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(3000);
	}

}
