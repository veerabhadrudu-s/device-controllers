package com.hpe.iot.http.vehant.vehiscan;

import static com.hpe.iot.http.test.constants.TestConstants.VEHANT;
import static com.hpe.iot.http.test.constants.TestConstants.VEHANT_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.VEHANT_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.JsonObject;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;
import com.hpe.iot.model.DeviceInfo;

public class VehantVehiScanTest extends HttpPluginTestBaseTemplate {

	@Test
	@DisplayName("test Publish Uplink Notification Scanned By Camera")
	public void testUplinkNotificationScannedByCamera() throws Exception {

		JsonObject expectedResponse = getExpectedSuccessResponse();
		MvcResult mvcResult = mockMvc
				.perform(post(formUplinkURL(VEHANT, VEHANT_MODEL, VEHANT_VERSION))
						.content(getPristechSmartParkingUplinkParkingEventMsg()).accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		assertEquals(expectedResponse, actualResponse, "Expected and Actual Responses are not same");
		validateConsumedUplinkMessage("AP13X1234");

	}

	private String getPristechSmartParkingUplinkParkingEventMsg() {
		return "{\"Vehicle\":{\"LicenseNum\":\"AP13X1234\",\"LPCategory\":\"Stolen\","
				+ "\"Time\":\"2017-05-0413:39:18\",\"TransId\":\"P01C020-2017050407238\","
				+ "\"VehicleColor\":\"Black\",\"CamName\":\"Lane1\",“Latitude”:”17.4012488”,“Longitude”:”78.4763849”,"
				+ "\"Description\":\"Vehicleisstolen\",\"imageUrl\":{\"image\":[\"http://10.10.1.119/TransId.jpg\",]}}}";

	}

	private void validateConsumedUplinkMessage(String vehicleNumber) throws InterruptedException {
		DeviceInfo deviceInfo = super.validateConsumedUplinkMessage(VEHANT, VEHANT_MODEL, VEHANT_VERSION, "Lane1");
		assertEquals(vehicleNumber,
				deviceInfo.getPayload().get("Vehicle").getAsJsonObject().get("LicenseNum").getAsString(),
				"Expected vehicle and actual numbers are not equal");
	}

}
