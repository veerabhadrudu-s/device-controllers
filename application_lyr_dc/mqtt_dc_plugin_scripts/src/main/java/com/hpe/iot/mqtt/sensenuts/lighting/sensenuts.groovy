/**
 * 
 */
package com.hpe.iot.mqtt.sensenuts.lighting

import static com.hpe.iot.utility.DataParserUtility.calculateUnsignedDecimalValFromSignedBytes
import static com.hpe.iot.utility.DataParserUtility.convertBytesToASCIIString
import static com.hpe.iot.utility.DataParserUtility.convertHexaToFloatPoint
import static java.util.Arrays.copyOfRange

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hpe.iot.model.DeviceModel
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher
import com.hpe.iot.utility.DataParserUtility

/**
 * @author sveera
 *
 */
public class SensenutsDeviceModel implements DeviceModel{

	@Override
	public String getManufacturer(){
		return "sensenuts"
	}

	@Override
	public String getModelId(){
		return "lighting"
	}
}


public class SensenutsPayloadDecipher implements PayloadDecipher{

	private final JsonParser jsonParser=new JsonParser();

	@Override
	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] payload) {
		switch(payload[0]) {
			case 0x62:
				return createForPeriodicNotification(payload);
			case 0x63:
				return createForAlert(payload);
		};
		throw new InvalidMessageType();
	}

	private JsonObject createForPeriodicNotification(byte[] payload) {
		int noOfLights=payload[5];
		final List<LightInfo> lightInfo=new ArrayList<>();
		for(int lightIndex=0;noOfLights>lightIndex;lightIndex++) {
			byte[] lightDetails=copyOfRange(payload, 6+(lightIndex*41), 47+(lightIndex*41));
			lightInfo.add(constructLightInfo(lightDetails));
		}
		Notification notification=new Notification(convertBytesToASCIIString(payload, 1, 4),"notification",lightInfo);
		return jsonParser.parse(new Gson().toJson(notification));
	}

	private LightInfo constructLightInfo(byte[] lightInfoBytes) {
		String lightId=convertBytesToASCIIString(lightInfoBytes,0,4);
		String voltage=Float.toString(convertHexaToFloatPoint(copyOfRange(lightInfoBytes, 4, 8)));
		String current=Float.toString(convertHexaToFloatPoint(copyOfRange(lightInfoBytes, 8, 12)));
		String frequency=Float.toString(convertHexaToFloatPoint(copyOfRange(lightInfoBytes, 12, 16)));
		String powerFactor=Float.toString(convertHexaToFloatPoint(copyOfRange(lightInfoBytes, 16, 20)));
		String consumption=Float.toString(convertHexaToFloatPoint(copyOfRange(lightInfoBytes, 20, 24)));
		String burnTime=calculateUnsignedDecimalValFromSignedBytes(copyOfRange(lightInfoBytes, 24, 32));
		String deviceUpTime=calculateUnsignedDecimalValFromSignedBytes(copyOfRange(lightInfoBytes, 32, 40));
		return new LightInfo(lightId, voltage, current, frequency, powerFactor, consumption, burnTime, deviceUpTime, Byte.toString(lightInfoBytes[40]));
	}

	private JsonObject createForAlert(byte[] payload) {
		Alert alert=new Alert(convertBytesToASCIIString(payload, 1, 4), "alert", convertBytesToASCIIString(payload, 5, 4), Byte.toString(payload[9]));
		return jsonParser.parse(new Gson().toJson(alert));
	}
}

public abstract class UplinkMessage {

	private final String gatewayId;
	private final String messageType;

	public UplinkMessage(String gatewayId, String messageType) {
		super();
		this.gatewayId = gatewayId;
		this.messageType = messageType;
	}
	@Override
	public String toString() {
		return "UplinkMessage [gatewayId=" + gatewayId + ", messageType=" + messageType + "]";
	}
}

public class Notification extends UplinkMessage{

	private final List<LightInfo> lightInfo;

	public Notification(String gatewayId, String messageType, List<LightInfo> lightInfo) {
		super(gatewayId, messageType);
		this.lightInfo = lightInfo;
	}

	@Override
	public String toString() {
		return "Notification [lightInfo=" + lightInfo +  super.toString() + "]";
	}
}

public class LightInfo{

	private final String id;
	private final String voltage;
	private final String current;
	private final String frequency;
	private final String powerFactor;
	private final String consumption;
	private final String burnTime;
	private final String deviceUpTime;
	private final String status;

	public LightInfo(String id, String voltage, String current, String frequency, String powerFactor,
	String consumption, String burnTime, String deviceUpTime, String status) {
		super();
		this.id = id;
		this.voltage = voltage;
		this.current = current;
		this.frequency = frequency;
		this.powerFactor = powerFactor;
		this.consumption = consumption;
		this.burnTime = burnTime;
		this.deviceUpTime = deviceUpTime;
		this.status = status;
	}

	@Override
	public String toString() {
		return "LightInfo [id=" + id + ", voltage=" + voltage + ", current=" + current + ", frequency=" + frequency
		+ ", powerFactor=" + powerFactor + ", consumption=" + consumption + ", burnTime=" + burnTime
		+ ", deviceUpTime=" + deviceUpTime + ", status=" + status + "]";
	}
}


public class Alert extends UplinkMessage{
	private final String lightId;
	private final String alertType;

	public Alert(String gatewayId, String messageType, String lightId, String alertType) {
		super(gatewayId, messageType);
		this.lightId = lightId;
		this.alertType = alertType;
	}

	@Override
	public String toString() {
		return "Alert [lightId=" + lightId + ", alertType=" + alertType  + super.toString() + "]";
	}
}


public class InvalidMessageType extends RuntimeException{
}

public class SensenutsDeviceIdExtractor implements DeviceIdExtractor{

	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload) {
		return payload.get("gatewayId").getAsString();
	}
}

public class SensenutsMessageTypeExtractor implements MessageTypeExtractor{

	@Override
	public String extractMessageType(DeviceModel deviceModel, JsonObject payload) {
		return payload.get("messageType").getAsString();
	}
}