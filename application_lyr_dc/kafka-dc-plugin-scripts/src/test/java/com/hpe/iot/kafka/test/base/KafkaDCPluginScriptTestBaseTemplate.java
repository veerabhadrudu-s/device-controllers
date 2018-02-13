/**
 * 
 */
package com.hpe.iot.kafka.test.base;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.gson.JsonParser;
import com.hpe.broker.service.producer.kafka.KafkaProducerService;
import com.hpe.iot.kafka.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.kafka.northbound.sdk.handler.mock.MockNorthboundDownlinkProducerService;

/**
 * @author sveera
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public abstract class KafkaDCPluginScriptTestBaseTemplate {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final JsonParser jsonParser = new JsonParser();

	@Autowired
	protected IOTDevicePayloadHolder iotDevicePayloadHolder;
	@Autowired
	protected MockNorthboundDownlinkProducerService mockNorthboundDownlinkProducerService;
	@Autowired
	protected KafkaProducerService<String, String> kafkaDevicePublisherService;
	
	@BeforeEach
	public void beforeTest() throws InterruptedException {
		waitForDCInitialization();
	}

	protected void tryPublishingMessage(String topicName, String payload) {
		try {
			kafkaDevicePublisherService.publishData(topicName, payload);
			waitForDCToCompletePayloadProcessing();
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		}
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

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(20000);
	}

	protected void waitForDCToCompletePayloadProcessing() throws InterruptedException {
		Thread.sleep(5000);
	}

}
