package com.hpe.iot.dc.groovyscript.validation.error.deviceIdExtractor;

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.model.DeviceInfo
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor
import com.hpe.iot.northbound.handler.outflow.PayloadCipher
import com.hpe.iot.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher
import com.hpe.iot.southbound.handler.inflow.impl.DefaultUplinkPayloadProcessor

/**
 * @author sveera
 *
 */
public class SampleDeviceModelHandler extends DefaultUplinkPayloadProcessor implements DeviceModel,PayloadDecipher,
MessageTypeExtractor,DownlinkPayloadProcessor,PayloadCipher{

	private final JsonParser jsonParser = new JsonParser();

	public SampleDeviceModelHandler(IOTPublisherService iotPublisherService) {
		super(iotPublisherService);
	}

	@Override
	public String getManufacturer(){
		return "Sample"
	}

	@Override
	public String getModelId(){
		return "Model"
	}
	@Override
	public String getVersion(){
		return "1.0"
	}

	@Override
	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] payload) {
		return (JsonObject) jsonParser.parse(new String(payload));
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

