/**
 * 
 */
package com.hpe.iot.kafka.southbound.service.inflow;

import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_VERSION;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public class KafkaSouthboundInflowServiceTest {

	@Autowired
	private DeviceModelFactory deviceModelFactory;
	@Autowired
	private SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	@Autowired
	private NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;

	@Before
	public void setUp() throws Exception {
		waitForDCInitialization();
	}

	@Test
	public void testDcInitialization() throws InterruptedException {
		waitForPayloadProcessing();
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
		Assert.assertEquals("Expected Device Model and Actual Device Models are not same",
				new GroovyScriptDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), deviceModel);
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
		Thread.sleep(1000);
	}

	private void waitForPayloadProcessing() throws InterruptedException {
		Thread.sleep(2000);
	}

}
