/**
 * 
 */
package com.hpe.iot.http.test.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.northbound.sdk.handler.mock.MockNorthboundDownlinkProducerService;
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
	protected final JsonParser jsonParser = new JsonParser();

	@Autowired
	protected IOTDevicePayloadHolder iotDevicePayloadHolder;
	@Autowired
	protected MockNorthboundDownlinkProducerService mockNorthboundDownlinkProducerService;

	@Autowired
	protected WebApplicationContext wac;
	protected MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		waitForDCInitialization();
	}

	protected void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(5000);
	}

	protected void waitForDCProcessing() throws InterruptedException {
		Thread.sleep(1000);
	}

	protected String formUplinkURL(final String manufacturer, final String modelId, final String version) {
		return SOUTHBOUND + "/" + manufacturer + "/" + modelId + "/" + version + "/";
	}

	protected JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	protected DeviceInfo validateConsumedUplinkMessage(String expectedManufacturer, String expectedModelId,
			String expectedVersion, String expectedDeviceId) throws InterruptedException {
		waitForDCProcessing();
		return validateConsumedUplinkMessageWithOutWait(expectedManufacturer, expectedModelId, expectedVersion,
				expectedDeviceId);
	}

	protected DeviceInfo validateConsumedUplinkMessageWithOutWait(String expectedManufacturer, String expectedModelId,
			String expectedVersion, String expectedDeviceId) throws InterruptedException {
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		assertNotNull(deviceInfo, "Device info cannot be null");
		validateDevice(deviceInfo.getDevice(), expectedManufacturer, expectedModelId, expectedVersion,
				expectedDeviceId);
		return deviceInfo;
	}

	protected DeviceInfo validateConsumedUplinkMessageDeviceModel(String expectedManufacturer, String expectedModelId,
			String expectedVersion) throws InterruptedException {
		waitForDCProcessing();
		return validateConsumedUplinkMessageDeviceModelWithOutWait(expectedManufacturer, expectedModelId,
				expectedVersion);
	}

	protected DeviceInfo validateConsumedUplinkMessageDeviceModelWithOutWait(String expectedManufacturer,
			String expectedModelId, String expectedVersion) throws InterruptedException {
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		assertNotNull(deviceInfo, "Device info cannot be null");
		validateDeviceModel(deviceInfo.getDevice(), expectedManufacturer, expectedModelId, expectedVersion);
		return deviceInfo;
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
