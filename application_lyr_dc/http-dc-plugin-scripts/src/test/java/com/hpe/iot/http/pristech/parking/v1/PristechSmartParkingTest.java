/**
 * 
 */
package com.hpe.iot.http.pristech.parking.v1;

import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH;
import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.PRISTECH_VERSION;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.JsonObject;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class PristechSmartParkingTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "3c003434";

	@Test
	public void testPristechSmartParkingUplinkParkingEvent() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(formUplinkURL(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION))
						.content(getPristechSmartParkingUplinkParkingEventMsg()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
		validateConsumedPristechUplinkMessage("PARKING_EVENT");

	}

	@Test
	public void testPristechSmartParkingUplinkHealthCheck() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(formUplinkURL(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION))
						.content(getPristechSmartParkingUplinkHealthCheckMsg()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
		validateConsumedPristechUplinkMessage("PARKING_HEALTH");
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

	private void validateConsumedPristechUplinkMessage(String expectedMessageType) throws InterruptedException {
		DeviceInfo deviceInfo = validateConsumedUplinkMessage(PRISTECH, PRISTECH_MODEL, PRISTECH_VERSION, DEVICE_ID);
		assertEquals("Expected and actual Message Type are not same", expectedMessageType, deviceInfo.getMessageType());
	}

}
