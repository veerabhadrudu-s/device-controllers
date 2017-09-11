package com.hpe.iot.http.avanseus.videoanalytics;

import com.google.gson.JsonObject
import com.hpe.iot.model.DeviceModel
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor
import com.jayway.jsonpath.JsonPath

public class AvanseusDeviceModel implements DeviceModel{

	@Override
	public String getManufacturer(){
		return "avanseus"
	}

	@Override
	public String getModelId(){
		return "videoanalytics"
	}
}

public class AvanseusDeviceIdExtractor implements DeviceIdExtractor{

	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload){
		String payloadString=payload.toString();
		return extractJSONString(payloadString, "\$.source.customerID")+"&"+extractJSONString(payloadString, "\$.source.deviceName");
	}

	private String extractJSONString(String jsonString, String jsonExpession) {
		return JsonPath.parse(jsonString).read(jsonExpession);
	}
}