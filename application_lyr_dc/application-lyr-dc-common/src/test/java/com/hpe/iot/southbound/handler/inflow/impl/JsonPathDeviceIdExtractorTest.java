/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import static com.hpe.iot.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModelImpl;

/**
 * @author sveera
 *
 */
public class JsonPathDeviceIdExtractorTest {

	private JsonPathDeviceIdExtractor jsonPathDeviceIdExtractor;

	@BeforeEach
	public void setUp() throws Exception {
		jsonPathDeviceIdExtractor = new JsonPathDeviceIdExtractor();
	}

	@Test
	@DisplayName("test extractDeviceId with Non JsonPathDeviceMetaModel For Exception")
	public final void testExtractDeviceIdWithNonJsonPathDeviceMetaModelAndExpectException() {
		assertThrows(RuntimeException.class, () -> jsonPathDeviceIdExtractor
				.extractDeviceId(new DeviceModelImpl(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), new JsonObject()));
	}

	@DisplayName("test extractDeviceId with ")
	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(DeviceIdExtractor_TestData_ArgumentsProvider.class)
	public final void testExtractDeviceIdForDeviceModels(
			JsonPathTypeExtractorTestData jsonPathMessageTypeExtractorData) {
		assertEquals(jsonPathMessageTypeExtractorData.getExpectedValue(),
				jsonPathDeviceIdExtractor.extractDeviceId(jsonPathMessageTypeExtractorData.getJsonPathDeviceMetaModel(),
						jsonPathMessageTypeExtractorData.getJsonData()));
	}

	private static class DeviceIdExtractor_TestData_ArgumentsProvider extends AbstractArgumentsProvider {
		@Override
		protected Map<String, String> expectedValuesForDeviceModels() {
			Map<String, String> expectedValuesForDeviceModels = new HashMap<>();
			expectedValuesForDeviceModels.put(TRACKIMO, "1129388");
			expectedValuesForDeviceModels.put(REXAWARE, "999");
			return expectedValuesForDeviceModels;
		}

	}

}
