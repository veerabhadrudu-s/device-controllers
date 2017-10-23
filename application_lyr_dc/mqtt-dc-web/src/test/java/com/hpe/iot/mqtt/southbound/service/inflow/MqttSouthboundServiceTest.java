/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.hpe.iot.mqtt.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SAMPLE_MODEL;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;
import com.hpe.iot.mqtt.test.constants.TestConstants;
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
@RunWith(SpringJUnit4ClassRunner.class)
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
	

	@Before
	public void beforeTest() throws InterruptedException, MqttException {
		waitForDCInitialization();
		mqttClient = new MqttClient(mqttServerUrl, "testcase-client", persistence);
		mqttClient.connect();
	}

	@Test
	@Ignore
	//Below Unit test is failing to compile as Groovy classes are compiled after java class are compiled.
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
	public void verifyDCSamplePluginScrip() {
		DeviceModel actualDeviceModel = deviceModelFactory.findDeviceModel(SAMPLE, SAMPLE_MODEL);
		DeviceIdExtractor deviceIdExtractor = southboundPayloadExtractorFactory.getDeviceIdExtractor(SAMPLE, SAMPLE_MODEL);
		MessageTypeExtractor messageTypeExtractor = southboundPayloadExtractorFactory.getMessageTypeExtractor(SAMPLE,
				SAMPLE_MODEL);
		PayloadDecipher payloadDecipher = southboundPayloadExtractorFactory.getPayloadDecipher(SAMPLE, SAMPLE_MODEL);
		UplinkPayloadProcessor uplinkPayloadProcessor = southboundPayloadExtractorFactory.getUplinkPayloadProcessor(SAMPLE,
				SAMPLE_MODEL);
		PayloadCipher payloadCipher=northboundPayloadExtractorFactory.getPayloadCipher(SAMPLE, SAMPLE_MODEL);
		DownlinkPayloadProcessor downlinkPayloadProcessor=northboundPayloadExtractorFactory.getDownlinkPayloadProcessor(SAMPLE, SAMPLE_MODEL);
		Assert.assertEquals("Expected Device Model and Actual Device Models are not same",
				new GroovyScriptDeviceModel(TestConstants.SAMPLE, TestConstants.SAMPLE_MODEL), actualDeviceModel);
		Assert.assertTrue("Expected DeviceIdExtractor and DeviceIdExtractor are not same",
				deviceIdExtractor instanceof DeviceIdExtractor);
		Assert.assertTrue("Expected MessageTypeExtractor and MessageTypeExtractor are not same",
				messageTypeExtractor instanceof MessageTypeExtractor);
		Assert.assertTrue("Expected PayloadDecipher and PayloadDecipher are not same",
				payloadDecipher instanceof PayloadDecipher);
		Assert.assertTrue("Expected UplinkPayloadProcessor and UplinkPayloadProcessor are not same",
				uplinkPayloadProcessor instanceof UplinkPayloadProcessor);
		Assert.assertTrue("Expected PayloadCipher and PayloadCipher are not same",
				payloadCipher instanceof PayloadCipher);
		Assert.assertTrue("Expected DownlinkPayloadProcessor and DownlinkPayloadProcessor are not same",
				downlinkPayloadProcessor instanceof DownlinkPayloadProcessor);
		
	}

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(3000);
	}


}
