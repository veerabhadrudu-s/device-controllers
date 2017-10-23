/**
 * 
 */
package com.hpe.iot.http.southbound.inflow.web;

import static com.hpe.iot.http.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.http.test.constants.TestConstants.REXAWARE_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.REXAWARE_VERSION;
import static com.hpe.iot.http.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.http.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.http.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.http.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.TRACKIMO_VERSION;
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
public class SouthboundServiceEndPointTest {

	private static final String SOUTHBOUND = "/southbound";
	private final JsonParser jsonParser = new JsonParser();

	@Autowired
	private DeviceModelFactory deviceModelFactory;
	@Autowired
	private SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	@Autowired
	private NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;
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
				.perform(post(SOUTHBOUND + "/" + "InvalidManufacturer" + "/" + "InvalidModel" + "/" + "1.0" + "/")
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
				.perform(post(SOUTHBOUND + "/" + TRACKIMO + "/" + TRACKIMO_MODEL + "/" + TRACKIMO_VERSION + "/")
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
				.perform(post(SOUTHBOUND + "/" + TRACKIMO + "/" + TRACKIMO_MODEL + "/" + TRACKIMO_VERSION + "/")
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
				.perform(post(SOUTHBOUND + "/" + TRACKIMO + "/" + TRACKIMO_MODEL + "/" + TRACKIMO_VERSION + "/")
						.content(getExpectedJSONStringForTrackimoSpeedNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void testProcessDevicePayloadForRexaWareBikeNotification() throws Exception {
		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(SOUTHBOUND + "/" + REXAWARE + "/" + REXAWARE_MODEL + "/" + REXAWARE_VERSION + "/")
						.content(createPayloadForRexaWareBikeNotification()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);
	}

	@Test
	public void verifyDCSamplePluginScrip() {
		DeviceModel actualDeviceModel = deviceModelFactory.findDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
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
				new GroovyScriptDeviceModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), actualDeviceModel);
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

	private JsonObject getExpectedDCInfo() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"This is a Generic HTTP DC \",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedFailureResponse() {
		String expectedResponseString = "{\"processingStatus\":\"FAILED\",\"otherInformation\":\"\",\"exceptionReason\":\"Device Model with manufacturer: InvalidManufacturer with modelId : InvalidModel with version :1.0 not supported.\"}";
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

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(1000);
	}

}
