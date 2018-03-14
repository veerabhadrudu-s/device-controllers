/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import static com.hpe.iot.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_DEV_ID_JPATH;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_DEV_ID_JPATH;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MSG_TYP_JPATH;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_VERSION;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;

/**
 * @author sveera
 *
 */
abstract class AbstractArgumentsProvider implements ArgumentsProvider {

	private final JsonParser jsonParser = new JsonParser();

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return Stream.of(prepareParameterizedTestDataCollection(expectedValuesForDeviceModels())).map(Arguments::of);
	}

	protected abstract Map<String, String> expectedValuesForDeviceModels();

	private JsonPathTypeExtractorTestData[] prepareParameterizedTestDataCollection(
			Map<String, String> expectedValuesForDeviceModels) {
		return new JsonPathTypeExtractorTestData[] {
				constructTrackimoJsonPathMessageTypeExtractorData(expectedValuesForDeviceModels.get(TRACKIMO)),
				constructRexawareJsonPathMessageTypeExtractorData(expectedValuesForDeviceModels.get(REXAWARE)) };
	}

	private JsonPathTypeExtractorTestData constructTrackimoJsonPathMessageTypeExtractorData(String expectedValue) {
		return constructJsonPathMessageTypeExtractorData(expectedValue, TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION,
				TRACKIMO_DEV_ID_JPATH, TRACKIMO_MSG_TYP_JPATH, getTrackimoDeviceData());
	}

	private String getTrackimoDeviceData() {
		return "{\"alarm_type\":\"Fence\",\"device_id\":\"1129388\",\"timestamp\":\"1462193525734\","
				+ "\"address\":\"14/310,ITPLMainRd,MaheswariNagar,BNarayanapura,Mahadevapura,Bengaluru,Karnataka560016,India\","
				+ "\"lat\":\"12.99726\",\"lng\":\"77.690885\",\"speed\":\"6\","
				+ "\"extras\":{\"name\":\"Testfence\"}}";
	}

	private JsonPathTypeExtractorTestData constructRexawareJsonPathMessageTypeExtractorData(String expectedValue) {
		return constructJsonPathMessageTypeExtractorData(expectedValue, REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION,
				REXAWARE_DEV_ID_JPATH, null, getRexawareDeviceData());
	}

	private String getRexawareDeviceData() {
		return "{\"DeviceData\":[{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\","
				+ "\"lat\":\"18.27363\",\"lon\":\"78.217891\",\"speed\":\"99\",\"altitude\":\"123\","
				+ "\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\","
				+ "\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\","
				+ "\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\","
				+ "\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\","
				+ "\"OBDIntakAirTemperature\":\"\",\"OBDAbsThrottlePosition\":\"\","
				+ "\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"},"
				+ "{\"DeviceID\":\"999\",\"AQDateTime\":\"dd/MM/yyyyHH:mm:ss\",\"lat\":\"18.27363\",\"lon\":\"78.217891\","
				+ "\"speed\":\"99\",\"altitude\":\"123\",\"gsmStrength\":\"5\",\"OBDDTCNumber\":\"\",\"OBDFuelSystemStatus\":\"\","
				+ "\"OBDCalEngineLoadValue\":\"\",\"OBDEngineCoolantTemperature\":\"\",\"OBDShortFuelTrim1\":\"\","
				+ "\"OBDLongTermFuelAdaption\":\"\",\"OBDIntakeManifoldPressure\":\"\",\"OBDEngineRPM\":\"\","
				+ "\"OBDVehicleSpeed\":\"\",\"OBDIgnitionAdvance\":\"\",\"OBDIntakAirTemperature\":\"\","
				+ "\"OBDAbsThrottlePosition\":\"\",\"OBDDistanceTravelledMIL\":\"\",\"OBDCommandedEGR\":\"\"}]}";
	}

	private JsonPathTypeExtractorTestData constructJsonPathMessageTypeExtractorData(String expectedMessageType,
			String manufacturer, String modelId, String version, String deviceIdJsonPath, String messageTypeJsonPath,
			String deviceDataJson) {
		JsonPathDeviceMetaModel jsonPathDeviceMetaModel = new JsonPathDeviceMetaModel(manufacturer, modelId, version,
				deviceIdJsonPath, messageTypeJsonPath);
		return new JsonPathTypeExtractorTestData(expectedMessageType, jsonPathDeviceMetaModel,
				(JsonObject) jsonParser.parse(deviceDataJson));
	}

}
