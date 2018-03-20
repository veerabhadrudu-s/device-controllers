/**
 * 
 */
package com.hpe.iot.kafka.southbound.service.inflow;

import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class KafkaSouthboundInflowServiceTest {

	private ClassPathXmlApplicationContext classPathXmlApplicationContext;
	private DeviceModelFactory deviceModelFactory;
	private SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	private NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;

	@BeforeEach
	public void setUp() throws Exception {
		classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/bean-config.xml");
		deviceModelFactory = classPathXmlApplicationContext.getBean(DeviceModelFactory.class);
		southboundPayloadExtractorFactory = classPathXmlApplicationContext
				.getBean(SouthboundPayloadExtractorFactory.class);
		northboundPayloadExtractorFactory = classPathXmlApplicationContext
				.getBean(NorthboundPayloadExtractorFactory.class);
	}

	@AfterEach
	public void tearDown() {
		classPathXmlApplicationContext.close();
	}

	@Test
	@DisplayName("test DC Initialization")
	public void testDcInitialization() throws InterruptedException {
		DeviceModel deviceModel = deviceModelFactory.findDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
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
		assertEquals(new GroovyScriptDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), deviceModel,
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

}
