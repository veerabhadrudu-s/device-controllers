/**
 * 
 */
package com.hpe.iot.http.libelium.smartagriculture;

import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM;
import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM_VERSION;
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
public class LibeliumSmartAgricultureProTest extends HttpPluginTestBaseTemplate {

	@Test
	public void testHttpSouthboundServiceForLibeliumSmartAgricultureProForNotificationMsgTyp() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		waitForDCInitialization();
		MvcResult mvcResult = mockMvc
				.perform(put(SOUTHBOUND + "/" + LIBELIUM + "/" + LIBELIUM_MODEL + "/" + LIBELIUM_VERSION + "/")
						.content(getLibeliumSmartAgricultureProForNotificationMsgTyp()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	private String getLibeliumSmartAgricultureProForNotificationMsgTyp() {
		return "{\"cmd\":\"rx\",\"seqno\":556,\"EUI\":\"0102030405060708\",\"ts\":1505749821468,\"fcnt\":6,\"port\":3,\"freq\":868300000,\"rssi\":-72,\"snr\":9.8,\"toa\":179,\"dr\":\"SF7 BW125 4/5\",\"ack\":false,\"bat\":255,\"data\":\"493a343038353139383136234e3a36234241543a34392354533a4d6f6e2c2031332f30352f30372c2030303a32343a3130235443423a32362e31362348554d423a36382e373923534f494c543a32362e3138234c573a302e30300d0a\"}";

	}

}
