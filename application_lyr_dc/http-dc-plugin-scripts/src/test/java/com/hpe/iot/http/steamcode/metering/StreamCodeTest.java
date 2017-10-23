/**
 * 
 */
package com.hpe.iot.http.steamcode.metering;

import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_VERSION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.JsonObject;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */
public class StreamCodeTest extends HttpPluginTestBaseTemplate {

	@Test
	public void testProcessDevicePayloadForStreamCodeScript() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		waitForDCInitialization();
		MvcResult mvcResult = mockMvc
				.perform(put(SOUTHBOUND + "/" + STREAMCODE + "/" + STREAMCODE_MODEL + "/" + STREAMCODE_VERSION + "/")
						.content(createPayloadForPayloadForStreamCodeNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	private String createPayloadForPayloadForStreamCodeNotification() {
		return "{\"2e3a3a40-80ea-11e7-9a7b-00259075adf2\": 285.0}";
	}

}
