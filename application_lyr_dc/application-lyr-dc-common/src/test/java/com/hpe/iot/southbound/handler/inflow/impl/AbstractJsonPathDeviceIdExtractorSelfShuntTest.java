package com.hpe.iot.southbound.handler.inflow.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.test.constants.TestConstants;

/**
 * @author sveera
 *
 */
public class AbstractJsonPathDeviceIdExtractorSelfShuntTest extends AbstractJsonPathDeviceIdExtractor {

	private final JsonParser jsonParser = new JsonParser();

	@Override
	public String getDeviceIdJsonPath() {
		return "$.device_id";
	}

	@Test
	public final void testExtractDeviceId() {
		assertEquals(
				this.extractDeviceId(
						new DeviceModelImpl(TestConstants.TRACKIMO, TestConstants.TRACKIMO_MODEL,
								TestConstants.TRACKIMO_VERSION),
						(JsonObject) jsonParser.parse(getTrackimoDeviceData())),
				"1129388", "Expected and actual device ids are not equal");
	}

	private String getTrackimoDeviceData() {
		return "{\"alarm_type\":\"Fence\",\"device_id\":\"1129388\",\"timestamp\":\"1462193525734\","
				+ "\"address\":\"14/310,ITPLMainRd,MaheswariNagar,BNarayanapura,Mahadevapura,Bengaluru,Karnataka560016,India\","
				+ "\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\","
				+ "\"extras\":{\"name\":\"Testfence\"}}";
	}

}
