package com.hpe.iot.mqtt.motwane.lighting;

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Base64.Decoder

import javax.xml.bind.DatatypeConverter

import org.apache.http.client.utils.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hpe.iot.model.Device
import com.hpe.iot.model.DeviceInfo
import com.hpe.iot.model.DeviceModel
import com.hpe.iot.mqtt.southbound.service.holder.GroovyServicesHolder
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor
import com.hpe.iot.southbound.handler.inflow.impl.AbstractJsonPathDeviceIdExtractor



public class MotwaneDeviceModel implements DeviceModel{

	@Override
	public String getManufacturer(){
		return "Motwane"
	}

	@Override
	public String getModelId(){
		return "Lighting"
	}
}

public class MotwaneLightingDeviceIdExtractor extends AbstractJsonPathDeviceIdExtractor{
	public String getDeviceIdJsonPath(){
		return "\$.deviceId";
	}
}

public class MotwaneLightingPayloadDecipher implements PayloadDecipher{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());

	//private final Decoder decoder=Base64.getDecoder();
	private final Decoder decoder=Base64.getMimeDecoder();
	private final JsonParser jsonParser=new JsonParser();

	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] rawPayload){
		JsonObject payload=jsonParser.parse(new String(rawPayload));
		String messageType=payload.get("messageId").getAsString();
		if(!messageType.equalsIgnoreCase("GSS"))
			return payload;
		logger.trace("Received encoded Json Payload : "+payload.toString());
		String encodedMessagePayload=payload.get("message").getAsString();
		logger.trace("Received encoded Payload Part : "+encodedMessagePayload);
		String decodedMessagePayload =new String(decoder.decode(encodedMessagePayload));
		JsonObject decodedJsonMessagePayload=jsonParser.parse(decodedMessagePayload).getAsJsonObject();
		payload.add("message",decodedJsonMessagePayload);
		return payload;
	}
}

public class MotwanePayloadProcessor implements UplinkPayloadProcessor{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	private final GroovyServicesHolder groovyServicesHolder;
	private final String weatherServiceCertifcatePath;
	private final JsonParser jsonParser=new JsonParser();
	private final DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private final DateFormat targetFormatDate = new SimpleDateFormat("dd-MM-yyyy");
	private final String dsmInternalIp="10.3.239.70"
	private final String dsmUserName="motwane";
	private final String dsmUserPassword="Motwane~1";
	private final String failureResponseFromDsm="Resource not found";

	public MotwanePayloadProcessor(GroovyServicesHolder groovyServicesHolder){
		this.groovyServicesHolder=groovyServicesHolder
		//this.weatherServiceCertifcatePath=System.getProperty("jboss.home.dir")+"/standalone/configuration/DC/mqttdc/script/cert/weather-der.cer";
		this.weatherServiceCertifcatePath="src/test/resources/script/cert/weather-der.cer";
		this.targetFormat.setTimeZone(TimeZone.getTimeZone("IST"));
	}

	public void processPayload(DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String messageType=payload.get("messageId").getAsString();
		switch(messageType){
			case "COA":sendSynchronizedDateMessageToDevice(decipheredPayload);
				Map<String,String> assetParamMap=readAssetParameters(decipheredPayload.getDevice().getDeviceId());
				sendInitializeControllerMessageToDevice(assetParamMap,decipheredPayload);
				sendSunshineAndSunSetForThirtyDaysForCOA(assetParamMap,decipheredPayload);
				break;
			case "GSS":sendSunshineAndSunSetForThirtyDaysForGSS(decipheredPayload);
				break;
			case "ALA":
				case "RDA":pushDataToUiotPlatform(decipheredPayload,"alarm");
				break;
			case "EOO_ACK":pushDataToUiotPlatform(decipheredPayload,"downlinkCommandStatus");
				break;
			default: logger.debug("Ignored payload is "+decipheredPayload);
		}
	}

	private void sendSynchronizedDateMessageToDevice(DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.get("switchInternalId").getAsString();
		String serverDateTime=targetFormat.format(new Date());
		Command command=new SYDCommand(getUniqueRequestId(decipheredPayload.getDevice()),"SYD",switchInternalId,decipheredPayload.getDevice().getDeviceId(),serverDateTime);
		publishDownlinkCommand(decipheredPayload.getDevice(), command);
	}

	private void sendInitializeControllerMessageToDevice(Map<String,String> assetParamMap,DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.get("switchInternalId").getAsString();
		Command command=new SPSCommand(getUniqueRequestId(decipheredPayload.getDevice()),"SPS",switchInternalId,decipheredPayload.getDevice().getDeviceId(),assetParamMap);
		publishDownlinkCommand(decipheredPayload.getDevice(), command)
	}

	private Map<String,String> readAssetParameters(String deviceId){
		final Map<String, String> headers = new HashMap<>();
		final String assetName="Motwane-"+deviceId;
		headers.put("Authorization", "Basic " + new String(Base64.getEncoder().encode((dsmUserName+":"+dsmUserPassword).getBytes())));
		String assetData = groovyServicesHolder.getHttpClientUtility()
				.getResourceOnHttp("http://"+dsmInternalIp+"/dsm/wsi/asset/getAssetByName/"+assetName, headers);
		if(assetData.equalsIgnoreCase(failureResponseFromDsm))
			throw new RuntimeException("Asset with name "+assetName+" not onboarded on UIOT");
		//return Collections.<String,String>emptyMap();
		JsonArray assetParams=jsonParser.parse(assetData).getAsJsonObject().get("assetAttrib").getAsJsonArray();
		Map<String,String> assetParamMap=new HashMap<>();
		for (JsonObject assetParam : assetParams.iterator())
			assetParamMap.put(assetParam.get("key").getAsString(), assetParam.get("value").getAsString());
		logger.trace("Asset Parameters for asset "+assetName+" are "+assetParamMap);
		return assetParamMap;
	}

	private void sendSunshineAndSunSetForThirtyDaysForCOA(Map<String,String> assetParamMap,DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.get("switchInternalId").getAsString()
		String latitude=assetParamMap.get("latitude");
		String longitude=assetParamMap.get("longitude");
		sendSunshineAndSunSetForThirtyDays(latitude, longitude, switchInternalId, decipheredPayload.getDevice());
	}

	private void sendSunshineAndSunSetForThirtyDaysForGSS(DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.getAsJsonObject("message").get("switchInternalId").getAsString()
		String latitude=payload.getAsJsonObject("message").get("latitude").getAsString();
		String longitude=payload.getAsJsonObject("message").get("longitude").getAsString();
		sendSunshineAndSunSetForThirtyDays(latitude, longitude, switchInternalId, decipheredPayload.getDevice());
	}

	private void sendSunshineAndSunSetForThirtyDays(String latitude,String longitude,String switchInternalId,Device device){
		List<SunriseSunset> sunriseSunsets=calcualteSunriseSunSetForCoordinates(latitude,longitude);
		SSSCommand sssCommand=new SSSCommand(getUniqueRequestId(device),"SSS",switchInternalId,device.getDeviceId(),sunriseSunsets);
		publishDownlinkCommand(device, sssCommand);
	}

	private String getUniqueRequestId(Device device){
		return device.getManufacturer()+"_"+device.getModelId()+"_"+device.getDeviceId()+"_"+new Date().getTime();
	}

	private void pushDataToUiotPlatform(DeviceInfo decipheredPayload,String containerName){
		groovyServicesHolder.getIotPublisherService().receiveDataFromDevice(decipheredPayload,containerName);
	}

	private List<SunriseSunset> calcualteSunriseSunSetForCoordinates(String latitude,String longitude){
		URI uri = new URIBuilder().setScheme("https").setHost("api.sunrise-sunset.org").setPort(443).setPath("/json")
				.setParameter("lat", latitude).setParameter("lng", longitude).setParameter("date", "today").setParameter("formatted", "0").build();
		String weatherServiceDataString=groovyServicesHolder.getHttpClientUtility().getResourceOnHttps(uri.toString(),weatherServiceCertifcatePath,false);
		JsonObject weatherServiceData=jsonParser.parse(weatherServiceDataString).getAsJsonObject();
		String sunrise=weatherServiceData.getAsJsonObject("results").get("sunrise").getAsString();
		String sunset=weatherServiceData.getAsJsonObject("results").get("sunset").getAsString();
		Date sunriseDate =DatatypeConverter.parseDateTime(sunrise).getTime();
		Date sunsetDate =DatatypeConverter.parseDateTime(sunset).getTime();
		String sunRiseString = targetFormat.format(sunriseDate);
		String sunsetString = targetFormat.format(sunsetDate);
		String sunTimeDateString=targetFormatDate.format(sunsetDate);
		SunriseSunset sunriseSunset=new SunriseSunset(sunRiseString,sunTimeDateString,sunsetString);
		List<SunriseSunset> sunriseSunsets=new ArrayList<>();
		sunriseSunsets.add(sunriseSunset);
		//Below code should be replaced with search weather api with date range.
		sunriseDate=targetFormat.parse(sunRiseString);
		sunsetDate=targetFormat.parse(sunsetString);
		Date sunTimeDate=targetFormatDate.parse(sunTimeDateString);
		Calendar sunriseCalander = Calendar.getInstance();
		Calendar sunsetCalander = Calendar.getInstance();
		Calendar sunTimeCalander = Calendar.getInstance();
		sunriseCalander.setTime(sunriseDate);
		sunsetCalander.setTime(sunsetDate);
		sunTimeCalander.setTime(sunTimeDate);
		for(int dayCounter=1;dayCounter<=29;dayCounter++){
			sunriseCalander.add(Calendar.DATE, 1);
			sunsetCalander.add(Calendar.DATE, 1);
			sunTimeCalander.add(Calendar.DATE, 1);
			sunriseSunsets.add(new SunriseSunset(targetFormat.format(sunriseCalander.getTime()),targetFormatDate.format(sunTimeCalander.getTime()),targetFormat.format(sunsetCalander.getTime())));
		}
		return sunriseSunsets;
	}

	public void publishDownlinkCommand(Device device,Command command){
		String sssDownlinkCommand=new Gson().toJson(command);
		DeviceInfo downlinkDeviceInfo=new DeviceInfo(device,command.getMessageId(), jsonParser.parse(sssDownlinkCommand).getAsJsonObject());
		groovyServicesHolder.getSouthboundPublisherService().publishPayload(downlinkDeviceInfo.getDevice(),downlinkDeviceInfo);
	}

	private class SunriseSunset{

		private final String SunriseTime;
		private final String SunTimeDate;
		private final String SunsetTime;

		public SunriseSunset(String sunriseTime, String sunTimeDate, String sunsetTime) {
			super();
			this.SunriseTime = sunriseTime;
			this.SunTimeDate = sunTimeDate;
			this.SunsetTime = sunsetTime;
		}

		public String getSunriseTime() {
			return SunriseTime;
		}

		public String getSunTimeDate() {
			return SunTimeDate;
		}

		public String getSunsetTime() {
			return SunsetTime;
		}

		@Override
		public String toString() {
			return "SSSCommand [SunriseTime=" + SunriseTime + ", SunTimeDate=" + SunTimeDate + ", SunsetTime=" + SunsetTime+
					"]";
		}
	}

	private abstract class Command{
		private final String requestId;
		private final String messageId;
		private final String switchInternalId;
		private final String deviceId;

		public Command(String requestId, String messageId, String switchInternalId, String deviceId){
			this.requestId = requestId;
			this.messageId = messageId;
			this.switchInternalId = switchInternalId;
			this.deviceId = deviceId;
		}

		public String getRequestId() {
			return requestId;
		}

		public String getMessageId() {
			return messageId;
		}

		public String getSwitchInternalId() {
			return switchInternalId;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public String getCommandId() {
			return commandId;
		}

	}


	private class SSSCommand extends Command{

		private final List<SunriseSunset> SunriseSunsetList;

		public SSSCommand(String requestId, String messageId, String switchInternalId, String deviceId,
		List<SunriseSunset> sunriseSunsetList) {
			super(requestId,messageId,switchInternalId,deviceId);
			this.SunriseSunsetList = sunriseSunsetList;
		}

		public List<SunriseSunset> getSunriseSunsetList() {
			return SunriseSunsetList;
		}

		@Override
		public String toString() {
			return "SSSCommand [SunriseSunsetList=" + SunriseSunsetList + ", getRequestId()=" + getRequestId()
			+ ", getMessageId()=" + getMessageId() + ", getSwitchInternalId()=" + getSwitchInternalId()
			+ ", getDeviceId()=" + getDeviceId() + ", getCommandId()=" + getCommandId() + "]";
		}

	}

	private class SYDCommand extends Command{

		private final ServerDateTime;

		public SYDCommand(String requestId, String messageId, String switchInternalId, String deviceId,
		String ServerDateTime) {
			super(requestId,messageId,switchInternalId,deviceId);
			this.ServerDateTime = ServerDateTime;
		}

		public String getServerDateTime() {
			return ServerDateTime;
		}

		@Override
		public String toString() {
			return "SYDCommand [ServerDateTime=" + ServerDateTime + ", getRequestId()=" + getRequestId()+
					", getMessageId()=" + getMessageId() + ", getSwitchInternalId()=" + getSwitchInternalId()+
					", getDeviceId()=" + getDeviceId() + ", getCommandId()=" + getCommandId() + "]";
		}
	}

	private class SPSCommand extends Command{

		private final String applySunsetSunrise;
		private final double latitude;
		private final double longitude;
		private final long updationInterval;
		private final String switchRFCommand;
		private final long phase1LampCount;
		private final long phase2LampCount;
		private final long phase3LampCount;
		private final long numofLamp;
		private final double ph1VolMin;
		private final double ph2VolMin;
		private final double ph3VolMin;
		private final double ph1VolMax;
		private final double ph2VolMax;
		private final double ph3VolMax;
		private final double ph1CurrMin;
		private final double ph2CurrMin;
		private final double ph3CurrMin;
		private final double ph1CurrMax;
		private final double ph2CurrMax;
		private final double ph3CurrMax;
		private final String switchShortName;

		public SPSCommand(String requestId, String messageId, String switchInternalId, String deviceId,
		Map<String,String> assetParams) {
			super(requestId,messageId,switchInternalId,deviceId);
			this.applySunsetSunrise=assetParams.get("applySunsetSunrise");
			this.latitude=Double.parseDouble(assetParams.get("latitude"));
			this.longitude=Double.parseDouble(assetParams.get("longitude"));
			this.switchRFCommand=assetParams.get("switchRFCommand");
			this.phase1LampCount=Long.parseLong(assetParams.get("phase1LampCount"));
			this.phase2LampCount=Long.parseLong(assetParams.get("phase2LampCount"));
			this.phase3LampCount=Long.parseLong(assetParams.get("phase3LampCount"));
			this.numofLamp=Long.parseLong(assetParams.get("numofLamp"));
			this.ph1VolMin=Double.parseDouble(assetParams.get("ph1VolMin"));
			this.ph3VolMin=Double.parseDouble(assetParams.get("ph2VolMin"));
			this.ph3VolMin=Double.parseDouble(assetParams.get("ph3VolMin"));
			this.ph1VolMax=Double.parseDouble(assetParams.get("ph1VolMax"));
			this.ph2VolMax=Double.parseDouble(assetParams.get("ph2VolMax"));
			this.ph3VolMax=Double.parseDouble(assetParams.get("ph3VolMax"));
			this.ph1CurrMin=Double.parseDouble(assetParams.get("ph1CurrMin"));
			this.ph2CurrMin=Double.parseDouble(assetParams.get("ph2CurrMin"));
			this.ph3CurrMin=Double.parseDouble(assetParams.get("ph3CurrMin"));
			this.ph1CurrMax=Double.parseDouble(assetParams.get("ph1CurrMax"));
			this.ph2CurrMax=Double.parseDouble(assetParams.get("ph2CurrMax"));
			this.ph3CurrMax=Double.parseDouble(assetParams.get("ph3CurrMax"));
			this.switchShortName=assetParams.get("switchShortName");
		}

		public String getApplySunsetSunrise() {
			return applySunsetSunrise;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public long getUpdationInterval() {
			return updationInterval;
		}

		public String getSwitchRFCommand() {
			return switchRFCommand;
		}

		public long getPhase1LampCount() {
			return phase1LampCount;
		}

		public long getPhase2LampCount() {
			return phase2LampCount;
		}

		public long getPhase3LampCount() {
			return phase3LampCount;
		}

		public long getNumofLamp() {
			return numofLamp;
		}

		public double getPh1VolMin() {
			return ph1VolMin;
		}

		public double getPh2VolMin() {
			return ph2VolMin;
		}

		public double getPh3VolMin() {
			return ph3VolMin;
		}

		public double getPh1VolMax() {
			return ph1VolMax;
		}

		public double getPh2VolMax() {
			return ph2VolMax;
		}

		public double getPh3VolMax() {
			return ph3VolMax;
		}

		public double getPh1CurrMin() {
			return ph1CurrMin;
		}

		public double getPh2CurrMin() {
			return ph2CurrMin;
		}

		public double getPh3CurrMin() {
			return ph3CurrMin;
		}

		public double getPh1CurrMax() {
			return ph1CurrMax;
		}

		public double getPh2CurrMax() {
			return ph2CurrMax;
		}

		public double getPh3CurrMax() {
			return ph3CurrMax;
		}

		public String getSwitchShortName() {
			return switchShortName;
		}

		@Override
		public String toString() {
			return "SPSCommand [applySunsetSunrise=" + applySunsetSunrise + ", latitude=" + latitude + ", longitude="+
					longitude + ", updationInterval=" + updationInterval + ", switchRFCommand=" + switchRFCommand+
					", phase1LampCount=" + phase1LampCount + ", phase2LampCount=" + phase2LampCount+
					", phase3LampCount=" + phase3LampCount + ", numofLamp=" + numofLamp + ", ph1VolMin=" + ph1VolMin+
					", ph2VolMin=" + ph2VolMin + ", ph3VolMin=" + ph3VolMin + ", ph1VolMax=" + ph1VolMax+
					", ph2VolMax=" + ph2VolMax + ", ph3VolMax=" + ph3VolMax + ", ph1CurrMin=" + ph1CurrMin+
					", ph2CurrMin=" + ph2CurrMin + ", ph3CurrMin=" + ph3CurrMin + ", ph1CurrMax=" + ph1CurrMax+
					", ph2CurrMax=" + ph2CurrMax + ", ph3CurrMax=" + ph3CurrMax + ", switchShortName="+
					switchShortName + ", getRequestId()=" + getRequestId() + ", getMessageId()=" + getMessageId()+
					", getSwitchInternalId()=" + getSwitchInternalId() + ", getDeviceId()=" + getDeviceId()+
					", getCommandId()=" + getCommandId() + "]";
		}

	}
}

public class MotwaneDownlinkPayloadProcessor implements DownlinkPayloadProcessor{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	private final GroovyServicesHolder groovyServicesHolder;

	public MotwaneDownlinkPayloadProcessor(GroovyServicesHolder groovyServicesHolder) {
		super();
		this.groovyServicesHolder = groovyServicesHolder;
	}

	@Override
	public void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload) {
		JsonObject downlinkCommand=decipheredPayload.getPayload();
		if(downlinkCommand.get("requestId")==null||downlinkCommand.get("requestId").getAsString()==null)
			downlinkCommand.addProperty("requestId", getUniqueRequestId(decipheredPayload.getDevice()));
		groovyServicesHolder.getSouthboundPublisherService().publishPayload(deviceModel, decipheredPayload);
		groovyServicesHolder.getIotPublisherService().receiveDataFromDevice(decipheredPayload, "downlinkCommandRequestId");
	}

	private String getUniqueRequestId(Device device){
		return device.getManufacturer()+"_"+device.getModelId()+"_"+device.getDeviceId()+"_"+new Date().getTime();
	}

}
