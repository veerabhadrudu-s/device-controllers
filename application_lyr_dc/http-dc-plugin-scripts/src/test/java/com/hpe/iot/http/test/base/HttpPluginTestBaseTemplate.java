/**
 * 
 */
package com.hpe.iot.http.test.base;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.activemq.ActiveMQConsumerService;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.northbound.sdk.handler.mock.MockNorthboundDownlinkProducerService;
import com.hpe.iot.http.northbound.sdk.handler.mock.MockNorthboundUplinkConsumerService;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public abstract class HttpPluginTestBaseTemplate {
	
	protected static final String SOUTHBOUND = "/southbound";
	private static final int DC_PROCESSING_WAIT_PERIOD = 10000;
	protected final JsonParser jsonParser = new JsonParser();

	@Autowired
	protected MockNorthboundDownlinkProducerService mockNorthboundDownlinkProducerService;
	@Autowired
	protected WebApplicationContext webApplicationContext;

	protected MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	protected String formUplinkURL(final String manufacturer, final String modelId, final String version) {
		return SOUTHBOUND + "/" + manufacturer + "/" + modelId + "/" + version + "/";
	}

	protected IOTDevicePayloadHolder postUplinkMessages(final String manufacturer, final String modelId,
			final String version, String... uplinkPayloads) {
		return postUplinkMessages(manufacturer, modelId, version, uplinkPayloads.length, uplinkPayloads);
	}

	protected IOTDevicePayloadHolder postUplinkMessages(final String manufacturer, final String modelId,
			final String version, int expectedUplinkMessagesCount, String... uplinkPayloads) {
		byte[][] binaryUplinkPayloads = new byte[uplinkPayloads.length][];
		for (int i = 0; i < uplinkPayloads.length; i++)
			binaryUplinkPayloads[i] = uplinkPayloads[i].getBytes();
		return postUplinkMessages(manufacturer, modelId, version, expectedUplinkMessagesCount, binaryUplinkPayloads);
	}

	protected IOTDevicePayloadHolder postUplinkMessages(final String manufacturer, final String modelId,
			final String version, int expectedUplinkMessagesCount, byte[]... uplinkPayloads) {
		List<MockHttpServletRequestBuilder> mockHttpServletRequestBuilders = new ArrayList<>();
		for (byte[] uplinkPayload : uplinkPayloads)
			mockHttpServletRequestBuilders.add(post(formUplinkURL(manufacturer, modelId, version))
					.content(uplinkPayload).accept("application/json"));
		return publishUplinkMessages(manufacturer, modelId, version, expectedUplinkMessagesCount,
				mockHttpServletRequestBuilders);
	}

	protected IOTDevicePayloadHolder putUplinkMessages(final String manufacturer, final String modelId,
			final String version, String... uplinkPayloads) {
		return postUplinkMessages(manufacturer, modelId, version, uplinkPayloads.length, uplinkPayloads);
	}

	protected IOTDevicePayloadHolder putUplinkMessages(final String manufacturer, final String modelId,
			final String version, int expectedUplinkMessagesCount, String... uplinkPayloads) {
		byte[][] binaryUplinkPayloads = new byte[uplinkPayloads.length][];
		for (int i = 0; i < uplinkPayloads.length; i++)
			binaryUplinkPayloads[i] = uplinkPayloads[i].getBytes();

		return postUplinkMessages(manufacturer, modelId, version, expectedUplinkMessagesCount, binaryUplinkPayloads);
	}

	protected IOTDevicePayloadHolder putUplinkMessages(final String manufacturer, final String modelId,
			final String version, int expectedUplinkMessagesCount, byte[]... uplinkPayloads) {
		List<MockHttpServletRequestBuilder> mockHttpServletRequestBuilders = new ArrayList<>();
		for (byte[] uplinkPayload : uplinkPayloads)
			mockHttpServletRequestBuilders.add(put(formUplinkURL(manufacturer, modelId, version)).content(uplinkPayload)
					.accept("application/json"));
		return publishUplinkMessages(manufacturer, modelId, version, expectedUplinkMessagesCount,
				mockHttpServletRequestBuilders);
	}

	private IOTDevicePayloadHolder publishUplinkMessages(final String manufacturer, final String modelId,
			final String version, int expectedUplinkMessagesCount,
			List<MockHttpServletRequestBuilder> mockHttpServletRequestBuilders) {
		CountDownLatch countDownLatch = new CountDownLatch(expectedUplinkMessagesCount);
		IOTDevicePayloadHolder iotDevicePayloadHolder = new IOTDevicePayloadHolder(countDownLatch);
		BrokerConsumerService<String> brokerConsumerService = new ActiveMQConsumerService(
				getSpringPropertyFileValue("${activemq.brokerURL}"), getSpringPropertyFileValue("${activemq.usename}"),
				getSpringPropertyFileValue("${activemq.password}"));
		MockNorthboundUplinkConsumerService mockNorthboundUplinkConsumerService = new MockNorthboundUplinkConsumerService(
				getSpringPropertyFileValue("${iot.device.uplink.destination}"), brokerConsumerService,
				iotDevicePayloadHolder);
		try {
			mockNorthboundUplinkConsumerService.startService();
			JsonObject expectedResponse = getExpectedSuccessResponse();
			for (MockHttpServletRequestBuilder mockHttpServletRequestBuilder : mockHttpServletRequestBuilders)
				postUplinkMessage(manufacturer, modelId, version, expectedResponse, mockHttpServletRequestBuilder);
			countDownLatch.await(DC_PROCESSING_WAIT_PERIOD, MILLISECONDS);
		} catch (Exception ex) {
			logExceptionStackTrace(ex, getClass());
			fail("Failed to execute unit test case for " + new DeviceModelImpl(manufacturer, modelId, version));
		} finally {
			mockNorthboundUplinkConsumerService.stopService();
		}
		return iotDevicePayloadHolder;
	}

	private void postUplinkMessage(final String manufacturer, final String modelId, final String version,
			JsonObject expectedResponse, MockHttpServletRequestBuilder mockHttpServletRequestBuilder)
			throws Exception, UnsupportedEncodingException {
		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		assertEquals(expectedResponse, actualResponse);
	}

	private String getSpringPropertyFileValue(String propertyName) {
		return webApplicationContext instanceof GenericWebApplicationContext
				? ((GenericWebApplicationContext) webApplicationContext).getBeanFactory()
						.resolveEmbeddedValue(propertyName)
				: null;
	}

	private JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	protected void validateConsumedUplinkMessage(String expectedManufacturer, String expectedModelId,
			String expectedVersion, String expectedDeviceId, DeviceInfo deviceInfo) throws InterruptedException {
		assertNotNull(deviceInfo, "Device info cannot be null");
		validateDevice(deviceInfo.getDevice(), expectedManufacturer, expectedModelId, expectedVersion,
				expectedDeviceId);
	}

	protected void validateConsumedUplinkMessageDeviceModel(String expectedManufacturer, String expectedModelId,
			String expectedVersion, DeviceInfo deviceInfo) throws InterruptedException {
		assertNotNull(deviceInfo, "Device info cannot be null");
		validateDeviceModel(deviceInfo.getDevice(), expectedManufacturer, expectedModelId, expectedVersion);
	}

	private void validateDevice(Device device, String expectedManufacturer, String expectedModelId,
			String expectedVersion, String expectedDeviceId) {
		validateDeviceModel(device, expectedManufacturer, expectedModelId, expectedVersion);
		assertEquals(expectedDeviceId, device.getDeviceId(), "Expected and Actual DeviceId are not same");
	}

	private void validateDeviceModel(Device device, String expectedManufacturer, String expectedModelId,
			String expectedVersion) {
		assertEquals(expectedManufacturer, device.getManufacturer(), "Expected and Actual Manufacturer are not same");
		assertEquals(expectedModelId, device.getModelId(), "Expected and Actual Model are not same");
		assertEquals(expectedVersion, device.getVersion(), "Expected and Actual Version are not same");
	}

}
