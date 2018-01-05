/**
 * 
 */
package com.hpe.iot.http.steamcode.metering;

import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_VERSION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.JsonObject;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */
public class StreamCodeTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "2e3a3a40-80ea-11e7-9a7b-00259075adf2";

	@Test
	@DisplayName("test Process DevicePayload For Stream Code Script")
	public void testProcessDevicePayloadForStreamCodeScript() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		waitForDCInitialization();
		MvcResult mvcResult = mockMvc
				.perform(put(formUplinkURL(STREAMCODE, STREAMCODE_MODEL, STREAMCODE_VERSION))
						.content(createPayloadForPayloadForStreamCodeNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assertions.assertEquals(expectedResponse, actualResponse, "Expected and Actual Responses are not same");
		validateConsumedUplinkMessage(STREAMCODE, STREAMCODE_MODEL, STREAMCODE_VERSION, DEVICE_ID);
	}

	private String createPayloadForPayloadForStreamCodeNotification() {
		return "{\"" + DEVICE_ID + "\": 285.0}";
	}

}
