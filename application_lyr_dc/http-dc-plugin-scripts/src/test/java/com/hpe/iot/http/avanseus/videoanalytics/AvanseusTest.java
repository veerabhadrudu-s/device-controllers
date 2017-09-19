/**
 * 
 */
package com.hpe.iot.http.avanseus.videoanalytics;

import static com.hpe.iot.http.test.constants.TestConstants.AVANSEUS;
import static com.hpe.iot.http.test.constants.TestConstants.AVANSEUS_MODEL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

public class AvanseusTest extends HttpPluginTestBaseTemplate {

	@Test
	public void testProcessDevicePayloadForAvanseusScript() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + AVANSEUS + "/" + AVANSEUS_MODEL)
						.content(createPayloadForPayloadForAvanseusNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	private String createPayloadForPayloadForAvanseusNotification() {
		return "{\"event\":{\"eventID\":\"595e3afdacccc36b0db916789\",\"eventType\":\"alert\",\"eventTime\":\"2017-04-19T13:22:41.121+0000\",\"eventMessage\":\"Dearmember1,Intrusionhasbeennoticedatthepremisesofidsuser.Viewclipathttp://ar34et\"},\"source\":{\"customerID\":\"idsuser\",\"deviceName\":\"Camera1\",\"IPAddress\":\"10.2.2.201\"},\"additionalInformation\":{\"receivers\":[{\"name\":\"member1\",\"phone\":[\"9620172550\",\"8951432882\",\"9880275453\",\"9811619401\"],\"emailID\":[\"abc@gmail.com\",\"myuser@gmail.com\",\"idsuser@gmail.com\"]},{\"name\":\"member2\",\"phone\":[\"7620182550\",\"9841532882\",\"8880275453\",\"8876619401\"],\"emailID\":[\"xyz@gmail.com\",\"user@gmail.com\",\"abc@gmail.com\"]}],\"whitelistedUser\":[{\"name\":\"raj\"},{\"name\":\"madan\"}],\"videoURL\":\"http://10.2.2.69/INT_VIdeos/123456.mp4\"}}";
	}

}
