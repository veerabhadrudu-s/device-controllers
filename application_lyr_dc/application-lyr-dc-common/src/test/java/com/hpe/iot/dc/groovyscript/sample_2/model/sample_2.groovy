package com.hpe.iot.dc.groovyscript.sample_2.model;

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.handson.logger.LiveLogger
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.model.DeviceInfo
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor
import com.hpe.iot.northbound.handler.outflow.PayloadCipher
import com.hpe.iot.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher
import com.hpe.iot.southbound.handler.inflow.impl.DefaultUplinkPayloadProcessor
import com.jayway.jsonpath.JsonPath

/**
 * @author sveera
 *
 */
public class SampleDeviceModel implements DeviceModel{

	@Override
	public String getManufacturer(){
		return "Sample_2"
	}

	@Override
	public String getModelId(){
		return "SampleModel"
	}
	@Override
	public String getVersion(){
		return "1.0"
	}
}

public class SampleDeviceModelHandler  extends DefaultUplinkPayloadProcessor implements PayloadDecipher,
MessageTypeExtractor,DeviceIdExtractor,DownlinkPayloadProcessor,PayloadCipher {

	private final JsonParser jsonParser = new JsonParser();

	public SampleDeviceModelHandler(LiveLogger liveLogger,IOTPublisherService iotPublisherService) {
		super(iotPublisherService);
	}

	@Override
	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] payload) {
		return (JsonObject) jsonParser.parse(new String(payload));
	}
	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload) {
		return JsonPath.parse(payload.toString()).read("\$.deviceId");
	}

	@Override
	public String extractMessageType(DeviceModel deviceModel, JsonObject payload) {
		return "default";
	}

	@Override
	public JsonObject cipherPayload(DeviceModel deviceModel, JsonObject payload) {
		return payload;
	}

	@Override
	public void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload) {
	}
}

