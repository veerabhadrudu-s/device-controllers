/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;

/**
 * @author sveera
 *
 */
class JsonPathTypeExtractorTestData {
	private final String expectedValue;
	private final JsonPathDeviceMetaModel jsonPathDeviceMetaModel;
	private final JsonObject jsonData;

	JsonPathTypeExtractorTestData(String expectedMessageType, JsonPathDeviceMetaModel jsonPathDeviceMetaModel,
			JsonObject jsonData) {
		super();
		this.expectedValue = expectedMessageType;
		this.jsonPathDeviceMetaModel = jsonPathDeviceMetaModel;
		this.jsonData = jsonData;
	}

	public JsonPathDeviceMetaModel getJsonPathDeviceMetaModel() {
		return jsonPathDeviceMetaModel;
	}

	public JsonObject getJsonData() {
		return jsonData;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	@Override
	public String toString() {
		return jsonPathDeviceMetaModel.getManufacturer() + " device data and expect value as " + expectedValue;
	}

}
