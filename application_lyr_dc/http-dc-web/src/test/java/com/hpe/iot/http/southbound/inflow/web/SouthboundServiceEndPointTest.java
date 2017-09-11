/**
 * 
 */
package com.hpe.iot.http.southbound.inflow.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.http.test.constants.TestConstants;

/**
 * @author sveera
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public class SouthboundServiceEndPointTest {

	private static final String SOUTHBOUND = "/southbound";
	private final JsonParser jsonParser = new JsonParser();

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		waitForDCInitialization();
	}

	@Test
	public void testGetDCInfo() throws Exception {
		JsonObject expectedResponse = getExpectedDCInfo();
		MvcResult mvcResult = mockMvc.perform(get(SOUTHBOUND + "/dcinfo").accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);

	}

	@Test
	public void testProcessForInvalidModel() throws Exception {
		JsonObject expectedResponse = getExpectedFailureResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + "InvalidManufacturer" + "/" + "InvalidModel")
						.content(getExpectedJSONStringForSampleNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessForTrackimoFenceNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.TRACKIMO + "/" + TestConstants.TRACKIMO_MODEL)
						.content(getExpectedJSONStringForTrackimoFenceNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessForTrackimoMovingNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.TRACKIMO + "/" + TestConstants.TRACKIMO_MODEL)
						.content(getExpectedJSONStringForTrackimoMovingNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessForTrackimoSpeedNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.TRACKIMO + "/" + TestConstants.TRACKIMO_MODEL)
						.content(getExpectedJSONStringForTrackimoSpeedNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessForSampleNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.SAMPLE + "/" + TestConstants.SAMPLE_MODEL)
						.content(getExpectedJSONStringForSampleNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessDevicePayloadForRexaWareBikeNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.REXAWARE + "/" + TestConstants.REXAWARE_MODEL)
						.content(createPayloadForRexaWareBikeNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessDevicePayloadForAvanseusScript() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + TestConstants.AVANSEUS + "/" + TestConstants.AVANSEUS_MODEL)
						.content(createPayloadForPayloadForAvanseusNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	private JsonObject getExpectedDCInfo() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"This is a Generic HTTP DC \",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedFailureResponse() {
		String expectedResponseString = "{\"processingStatus\":\"FAILED\",\"otherInformation\":\"\",\"exceptionReason\":\"Device Model with manufacturer: InvalidManufacturer with modelId : InvalidModel not supported.\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private String getExpectedJSONStringForTrackimoFenceNotification() {
		return "{\"alarm_type\":\"Fence\",\"device_id\":\"1129388\",\"timestamp\":\"1462193525734\",\"address\":\"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India\",\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\",\"extras\":{\"name\":\"Test fence\"}}";
	}

	private String getExpectedJSONStringForTrackimoMovingNotification() {
		return "{\"alarm_type\":\"Moving\",\"device_id\":\"1129388\",\"timestamp\":\"1462193525734\",\"address\":\"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India\",\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\",\"extras\":{\"name\":\"Test fence\"}}";
	}

	private String getExpectedJSONStringForTrackimoSpeedNotification() {
		return "{\"alarm_type\":\"Speed\",\"device_id\":\"1129388\",\"timestamp\":\"1462193525734\",\"address\":\"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India\",\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\",\"extras\":{\"name\":\"Test fence\"}}";
	}

	private String getExpectedJSONStringForSampleNotification() {
		return "{\"message_type\":\"Notification\",\"sample_id\":\"Sample123354\",\"timestamp\":\"1462193525734\",\"address\":\"14/310, ITPL Main Rd, Maheswari Nagar, B Narayanapura, Mahadevapura, Bengaluru, Karnataka 560016, India\",\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\",\"extras\":{\"type\":\"Notification\"}}";
	}

	private String createPayloadForRexaWareBikeNotification() {
		return "{\"DeviceData\":[{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\",\"lat\":\"18.27363\",\"lon\":\"78.217891\",\"speed\":\"99\",\"altitude\":\"123\",\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\",\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\",\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\",\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\",\"OBDIntakAirTemperature\":\"\",\"OBDAbsThrottlePosition\":\"\",\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"},{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\",\"lat\":\"18.27363\",\"lon\":\"78.217891\",\"speed\":\"99\",\"altitude\":\"123\",\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\",\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\",\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\",\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\",\"OBDIntakAirTemperature\":\"\",\"OBDAbsThrottlePosition\":\"\",\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"}]}";
	}

	private String createPayloadForPayloadForAvanseusNotification() {
		return "{\"event\":{\"eventID\":\"595e3afdacccc36b0db916789\",\"eventType\":\"alert\",\"eventTime\":\"2017-04-19T13:22:41.121+0000\",\"eventMessage\":\"Dearmember1,Intrusionhasbeennoticedatthepremisesofidsuser.Viewclipathttp://ar34et\"},\"source\":{\"customerID\":\"idsuser\",\"deviceName\":\"Camera1\",\"IPAddress\":\"10.2.2.201\"},\"additionalInformation\":{\"receivers\":[{\"name\":\"member1\",\"phone\":[\"9620172550\",\"8951432882\",\"9880275453\",\"9811619401\"],\"emailID\":[\"abc@gmail.com\",\"myuser@gmail.com\",\"idsuser@gmail.com\"]},{\"name\":\"member2\",\"phone\":[\"7620182550\",\"9841532882\",\"8880275453\",\"8876619401\"],\"emailID\":[\"xyz@gmail.com\",\"user@gmail.com\",\"abc@gmail.com\"]}],\"whitelistedUser\":[{\"name\":\"raj\"},{\"name\":\"madan\"}],\"videoURL\":\"http://10.2.2.69/INT_VIdeos/123456.mp4\"}}";
	}

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(1000);
	}

}
