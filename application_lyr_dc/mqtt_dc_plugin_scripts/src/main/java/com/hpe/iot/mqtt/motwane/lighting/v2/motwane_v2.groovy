package com.hpe.iot.mqtt.motwane.lighting.v2;

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Base64.Decoder
import java.util.Base64.Encoder
import java.util.Arrays
import java.util.Base64
import java.util.List

import javax.xml.bind.DatatypeConverter

import org.apache.http.client.utils.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hpe.iot.dc.model.Device
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.model.DeviceInfo
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

	@Override
	public String getVersion() {
		return "2"
	}
}

public class MotwaneLightingDeviceIdExtractor extends AbstractJsonPathDeviceIdExtractor{
	public String getDeviceIdJsonPath(){
		return "\$.SwitchInternalId";
	}
}

public class MotwaneLightingPayloadDecipher implements PayloadDecipher{
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	//private final Decoder decoder=Base64.getDecoder();
	private final Decoder decoder=Base64.getMimeDecoder();
	private final JsonParser jsonParser=new JsonParser();
	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] rawPayload){
		String decodedMessagePayload =new String(decoder.decode(new String(rawPayload)));
		logger.trace("Payload after Base64 Decoding "+decodedMessagePayload);
		return jsonParser.parse(decodedMessagePayload).getAsJsonObject();
	}
}

public class MotwanePayloadProcessor implements UplinkPayloadProcessor{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	private final GroovyServicesHolder groovyServicesHolder;
	private final String weatherServiceCertifcatePath;
	private final JsonParser jsonParser=new JsonParser();
	private final DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private final DateFormat targetFormatDate = new SimpleDateFormat("dd-MM-yyyy");
	private final String dsmIp="10.3.239.70"
	//private final String dsmIp="185.170.48.160"
	private final String dsmUserName="motwane";
	private final String dsmUserPassword="Motwane~1";
	private final String failureResponseFromDsm="Resource not found";
	private final Encoder encoder = Base64.getMimeEncoder();

	public MotwanePayloadProcessor(GroovyServicesHolder groovyServicesHolder){
		this.groovyServicesHolder=groovyServicesHolder
		//this.weatherServiceCertifcatePath=System.getProperty("jboss.home.dir")+"/standalone/configuration/DC/mqttdc/script/cert/weather-der.cer";
		this.weatherServiceCertifcatePath="src/test/resources/script/cert/weather-der.cer";
		this.targetFormat.setTimeZone(TimeZone.getTimeZone("IST"));
	}

	public void processPayload(DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String eventType=payload.get("Event").getAsString();
		switch(eventType){
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
		String switchInternalId=payload.get("SwitchInternalId").getAsString();
		String serverDateTime=targetFormat.format(new Date());
		Command command=new SYDCommand(getUniqueRequestId(decipheredPayload.getDevice()),"SYD",switchInternalId,serverDateTime);
		publishDownlinkCommand(decipheredPayload.getDevice(), command);
	}

	private void sendInitializeControllerMessageToDevice(Map<String,String> assetParamMap,DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.get("SwitchInternalId").getAsString();
		Command command=new SPSCommand(getUniqueRequestId(decipheredPayload.getDevice()),"SPS",switchInternalId,assetParamMap);
		publishDownlinkCommand(decipheredPayload.getDevice(), command)
	}

	private Map<String,String> readAssetParameters(String deviceId){
		final Map<String, String> headers = new HashMap<>();
		final String assetName="Motwane-"+deviceId;
		headers.put("Authorization", "Basic " + new String(Base64.getEncoder().encode((dsmUserName+":"+dsmUserPassword).getBytes())));
		String assetData = groovyServicesHolder.getHttpClientUtility()
				.getResourceOnHttp("http://"+dsmIp+"/dsm/wsi/asset/getAssetByName/"+assetName, headers);
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
		String switchInternalId=payload.get("SwitchInternalId").getAsString()
		String latitude=assetParamMap.get("latitude");
		String longitude=assetParamMap.get("longitude");
		sendSunshineAndSunSetForThirtyDays(latitude, longitude, switchInternalId, decipheredPayload.getDevice());
	}

	private void sendSunshineAndSunSetForThirtyDaysForGSS(DeviceInfo decipheredPayload){
		JsonObject payload=decipheredPayload.getPayload();
		String switchInternalId=payload.get("SwitchInternalId").getAsString()
		String latitude=payload.get("Latitude").getAsString();
		String longitude=payload.get("Longitude").getAsString();
		sendSunshineAndSunSetForThirtyDays(latitude, longitude, switchInternalId, decipheredPayload.getDevice());
	}

	private void sendSunshineAndSunSetForThirtyDays(String latitude,String longitude,String switchInternalId,Device device){
		List<SunriseSunset> sunriseSunsets=calcualteSunriseSunSetForCoordinates(latitude,longitude);
		SunriseSunset[] sunriseSunsetsArray=sunriseSunsets.toArray(new SunriseSunset[sunriseSunsets.size()]);
		SSSCommand sssCommand=new SSSCommand(getUniqueRequestId(device),"SSS",switchInternalId,new Gson().toJson(sunriseSunsetsArray));
		publishDownlinkCommand(device, sssCommand);
	}

	private String getUniqueRequestId(Device device){
		return device.getManufacturer()+"_"+device.getModelId()+"_"+device.getVersion()+"_"+device.getDeviceId()+"_"+new Date().getTime();
	}

	private void pushDataToUiotPlatform(DeviceInfo decipheredPayload,String containerName){
		groovyServicesHolder.getIotPublisherService().receiveDataFromDevice(decipheredPayload,containerName);
	}

	private List<SunriseSunset> calcualteSunriseSunSetForCoordinates(String latitude,String longitude){
		URI uri = new URIBuilder().setScheme("https").setHost("api.sunrise-sunset.org").setPort(443).setPath("/json")
				.setParameter("lat", latitude).setParameter("lng", longitude).setParameter("date", "today").setParameter("formatted", "0").build();
		logger.debug("Weather Api URL "+ uri.toString());
		String weatherServiceDataString=groovyServicesHolder.getHttpClientUtility().getResourceOnHttps(uri.toString(),weatherServiceCertifcatePath,false);
		JsonObject weatherServiceData=jsonParser.parse(weatherServiceDataString).getAsJsonObject();
		String sunrise=weatherServiceData.getAsJsonObject("results").get("sunrise").getAsString();
		String sunset=weatherServiceData.getAsJsonObject("results").get("sunset").getAsString();
		Date sunriseDate =DatatypeConverter.parseDateTime(sunrise).getTime();
		Date sunsetDate =DatatypeConverter.parseDateTime(sunset).getTime();
		//To get sunrise for next day below code is a hack to avoid making two calls to weather API. 
		Calendar calander = Calendar.getInstance();
		calander.setTime(sunriseDate);
		calander.add(Calendar.DATE, 1);		
		String sunRiseString = targetFormat.format(calander.getTime());
		// Hack Ends
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
			sunriseSunsets.add(new SunriseSunset(targetFormat.format(sunriseCalander.getTime()),
					targetFormatDate.format(sunTimeCalander.getTime()),targetFormat.format(sunsetCalander.getTime())));
		}
		return sunriseSunsets;
	}

	public void publishDownlinkCommand(Device device,Command command){
		String sssDownlinkCommand=new Gson().toJson(command);
		DeviceInfo downlinkDeviceInfo=new DeviceInfo(device,command.getCommand(), jsonParser.parse(sssDownlinkCommand).getAsJsonObject());
		logger.debug("Acknowledged downlink message is " + downlinkDeviceInfo.getPayload().toString());
		groovyServicesHolder.getSouthboundPublisherService().publishPayload(downlinkDeviceInfo.getDevice(),device.getDeviceId(),
				encoder.encode(downlinkDeviceInfo.getPayload().toString().getBytes()));
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
			return "SunriseSunset [SunriseTime=" + SunriseTime + ", SunTimeDate=" + SunTimeDate + ", SunsetTime="+
					SunsetTime + "]";
		}
	}

	private abstract class Command{
		private final String ReqSrNo;
		private final String Command;
		private final String SwitchInternalId;

		public Command(String ReqSrNo, String Command, String SwitchInternalId){
			this.ReqSrNo = ReqSrNo;
			this.Command = Command;
			this.SwitchInternalId = SwitchInternalId;
		}

		public String getReqSrNo() {
			return ReqSrNo;
		}

		public String getCommand() {
			return Command;
		}

		public String getSwitchInternalId() {
			return switchInternalId;
		}
	}


	private class SSSCommand extends Command{

		private final String SunriseSunsetList;

		public SSSCommand(String ReqSrNo, String Command, String switchInternalId,
		String sunriseSunsetList) {
			super(ReqSrNo, Command, switchInternalId);
			SunriseSunsetList = sunriseSunsetList;
		}

		public String getSunriseSunsetList() {
			return SunriseSunsetList;
		}

		@Override
		public String toString() {
			return "SSSCommand [getSunriseSunsetList()=" + Arrays.toString(getSunriseSunsetList()) + ", getReqSrNo()="+
					getReqSrNo() + ", getCommand()=" + getCommand() + ", getSwitchInternalId()="+
					getSwitchInternalId() + "]";
		}
	}

	private class SYDCommand extends Command{

		private final String ServerDateTime;

		public SYDCommand(String ReqSrNo, String Command, String SwitchInternalId, String serverDateTime) {
			super(ReqSrNo, Command, SwitchInternalId);
			ServerDateTime = serverDateTime;
		}

		public String getServerDateTime() {
			return ServerDateTime;
		}

		@Override
		public String toString() {
			return "SYDCommand [getServerDateTime()=" + getServerDateTime() + ", getReqSrNo()=" + getReqSrNo()+
					", getCommand()=" + getCommand() + ", getSwitchInternalId()=" + getSwitchInternalId()+
					", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()+
					"]";
		}
	}

	private class SPSCommand extends Command{

		private final String SwitchName;
		private final String SwitchShortName;
		private final String Block1OnTime;
		private final String Block1OffTime;
		private final String Block2OnTime;
		private final String Block2OffTime;
		private final String Block3OnTime;
		private final String Block3OffTime;
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
		private final long NumofLamp;
		private final double Latitude;
		private final double Longitude;
		private final String GPSRealTime;
		private final long UpdationInterval;
		private final String SwitchRFCommand;
		private final long Phase1LampCount;
		private final long Phase2LampCount;
		private final long Phase3LampCount;
		private final String DefaultStartTime;
		private final String DefaultEndTime;

		public SPSCommand(String ReqSrNo, String Command, String switchInternalId,Map<String,String> assetParams) {
			super(ReqSrNo, Command, switchInternalId);
			this.SwitchName=assetParams.get("SwitchName");
			this.SwitchShortName=assetParams.get("switchShortName");
			this.Block1OnTime=assetParams.get("Block1OnTime");;
			this.Block1OffTime=assetParams.get("Block1OffTime");;
			this.Block2OnTime=assetParams.get("Block2OnTime");;
			this.Block2OffTime=assetParams.get("Block2OffTime");;
			this.Block3OnTime=assetParams.get("Block3OnTime");;
			this.Block3OffTime=assetParams.get("Block3OffTime");;
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
			this.NumofLamp=Long.parseLong(assetParams.get("numofLamp"));
			this.Latitude=Double.parseDouble(assetParams.get("latitude"));
			this.Longitude=Double.parseDouble(assetParams.get("longitude"));
			this.GPSRealTime=assetParams.get("GPSRealTime");
			this.UpdationInterval=Double.parseDouble(assetParams.get("UpdationInterval"));
			this.SwitchRFCommand=assetParams.get("switchRFCommand");
			this.Phase1LampCount=Long.parseLong(assetParams.get("phase1LampCount"));
			this.Phase2LampCount=Long.parseLong(assetParams.get("phase2LampCount"));
			this.Phase3LampCount=Long.parseLong(assetParams.get("phase3LampCount"));
			this.DefaultStartTime=assetParams.get("DefaultStartTime");
			this.DefaultEndTime=assetParams.get("DefaultEndTime");
		}

		public String getSwitchName() {
			return SwitchName;
		}

		public String getSwitchShortName() {
			return SwitchShortName;
		}

		public String getBlock1OnTime() {
			return Block1OnTime;
		}

		public String getBlock1OffTime() {
			return Block1OffTime;
		}

		public String getBlock2OnTime() {
			return Block2OnTime;
		}

		public String getBlock2OffTime() {
			return Block2OffTime;
		}

		public String getBlock3OnTime() {
			return Block3OnTime;
		}

		public String getBlock3OffTime() {
			return Block3OffTime;
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

		public long getNumofLamp() {
			return NumofLamp;
		}

		public double getLatitude() {
			return Latitude;
		}

		public double getLongitude() {
			return Longitude;
		}

		public String getGPSRealTime() {
			return GPSRealTime;
		}

		public long getUpdationInterval() {
			return UpdationInterval;
		}

		public String getSwitchRFCommand() {
			return SwitchRFCommand;
		}

		public long getPhase1LampCount() {
			return Phase1LampCount;
		}

		public long getPhase2LampCount() {
			return Phase2LampCount;
		}

		public long getPhase3LampCount() {
			return Phase3LampCount;
		}

		public String getDefaultStartTime() {
			return DefaultStartTime;
		}

		public String getDefaultEndTime() {
			return DefaultEndTime;
		}

		@Override
		public String toString() {
			return "SPSCommand [getSwitchName()=" + getSwitchName() + ", getSwitchShortName()=" + getSwitchShortName()+
					", getBlock1OnTime()=" + getBlock1OnTime() + ", getBlock1OffTime()=" + getBlock1OffTime()+
					", getBlock2OnTime()=" + getBlock2OnTime() + ", getBlock2OffTime()=" + getBlock2OffTime()+
					", getBlock3OnTime()=" + getBlock3OnTime() + ", getBlock3OffTime()=" + getBlock3OffTime()+
					", getPh1VolMin()=" + getPh1VolMin() + ", getPh2VolMin()=" + getPh2VolMin() + ", getPh3VolMin()="+
					getPh3VolMin() + ", getPh1VolMax()=" + getPh1VolMax() + ", getPh2VolMax()=" + getPh2VolMax()+
					", getPh3VolMax()=" + getPh3VolMax() + ", getPh1CurrMin()=" + getPh1CurrMin()+
					", getPh2CurrMin()=" + getPh2CurrMin() + ", getPh3CurrMin()=" + getPh3CurrMin()+
					", getPh1CurrMax()=" + getPh1CurrMax() + ", getPh2CurrMax()=" + getPh2CurrMax()+
					", getPh3CurrMax()=" + getPh3CurrMax() + ", getNumofLamp()=" + getNumofLamp() + ", getLatitude()="+
					getLatitude() + ", getLongitude()=" + getLongitude() + ", getGPSRealTime()=" + getGPSRealTime()+
					", getUpdationInterval()=" + getUpdationInterval() + ", getSwitchRFCommand()="+
					getSwitchRFCommand() + ", getPhase1LampCount()=" + getPhase1LampCount()+
					", getPhase2LampCount()=" + getPhase2LampCount() + ", getPhase3LampCount()="+
					getPhase3LampCount() + ", getDefaultStartTime()=" + getDefaultStartTime()+
					", getDefaultEndTime()=" + getDefaultEndTime() + ", getReqSrNo()=" + getReqSrNo()+
					", getCommand()=" + getCommand() + ", getSwitchInternalId()=" + getSwitchInternalId() + "]";
		}

	}
}

public class MotwaneDownlinkPayloadProcessor implements DownlinkPayloadProcessor{

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	private final GroovyServicesHolder groovyServicesHolder;
	private final Encoder encoder = Base64.getMimeEncoder();

	public MotwaneDownlinkPayloadProcessor(GroovyServicesHolder groovyServicesHolder) {
		super();
		this.groovyServicesHolder = groovyServicesHolder;
	}

	@Override
	public void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload) {
		JsonObject downlinkCommand=decipheredPayload.getPayload();
		if(downlinkCommand.get("ReqSrNo")==null||downlinkCommand.get("ReqSrNo").getAsString()==null)
			downlinkCommand.addProperty("ReqSrNo", getUniqueRequestId(decipheredPayload.getDevice()));
		logger.debug("Received downlink message is " + decipheredPayload.getPayload().toString());
		groovyServicesHolder.getSouthboundPublisherService().publishPayload(deviceModel,decipheredPayload.getDevice().getDeviceId(),
				encoder.encode(decipheredPayload.getPayload().toString().getBytes()));
		groovyServicesHolder.getIotPublisherService().receiveDataFromDevice(decipheredPayload, "downlinkCommandRequestId");
	}

	private String getUniqueRequestId(Device device){
		return device.getManufacturer()+"_"+device.getModelId()+"_"+device.getDeviceId()+"_"+new Date().getTime();
	}

}
