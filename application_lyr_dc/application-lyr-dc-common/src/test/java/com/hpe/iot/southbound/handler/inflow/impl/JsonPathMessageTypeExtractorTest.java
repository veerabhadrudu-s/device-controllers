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

public class JsonPathMessageTypeExtractorTest {

	private JsonPathMessageTypeExtractor jsonPathMessageTypeExtractor;

	@BeforeEach
	public void setUp() throws Exception {
		jsonPathMessageTypeExtractor = new JsonPathMessageTypeExtractor();
	}

	@Test
	@DisplayName("test extract MessageType with Non JsonPathDeviceMetaModel For Exception")
	public final void testExtractMessageTypeWithNonJsonPathDeviceMetaModelAndExpectException() {
		assertThrows(RuntimeException.class, () -> jsonPathMessageTypeExtractor
				.extractMessageType(new DeviceModelImpl(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION), new JsonObject()));
	}

	@DisplayName("test extractMessageType with ")
	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(MessageTypeExtractor_TestData_ArgumentsProvider.class)
	public final void testExtractMessageTypeForDeviceModels(
			JsonPathTypeExtractorTestData jsonPathMessageTypeExtractorData) {
		assertEquals(jsonPathMessageTypeExtractorData.getExpectedValue(),
				jsonPathMessageTypeExtractor.extractMessageType(
						jsonPathMessageTypeExtractorData.getJsonPathDeviceMetaModel(),
						jsonPathMessageTypeExtractorData.getJsonData()));
	}

	private static class MessageTypeExtractor_TestData_ArgumentsProvider extends AbstractArgumentsProvider {

		@Override
		protected Map<String, String> expectedValuesForDeviceModels() {
			Map<String, String> expectedValuesForDeviceModels = new HashMap<>();
			expectedValuesForDeviceModels.put(TRACKIMO, "Fence");
			expectedValuesForDeviceModels.put(REXAWARE, "default");
			return expectedValuesForDeviceModels;
		}

	}

}
