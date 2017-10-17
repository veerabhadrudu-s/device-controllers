/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.v1


import static com.hpe.iot.dc.util.DataParserUtility.calculateUnsignedDecimalValFromSignedBytes

import java.nio.channels.SocketChannel
import java.text.SimpleDateFormat

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hpe.iot.dc.model.Device
import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.service.MessageService
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory
import com.hpe.iot.dc.southbound.converter.inflow.session.SessionBasedUplinkDeviceDataConverter
import com.hpe.iot.dc.southbound.service.inflow.UplinkMessageService
import com.hpe.iot.dc.southbound.transformer.inflow.session.SessionBasedUplinkDataModelTransformer
import com.hpe.iot.dc.tcp.southbound.model.ProcessingTaskType
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel
import com.hpe.iot.dc.tcp.southbound.model.TCPOptions
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter
import com.hpe.iot.dc.tcp.southbound.socketpool.ClientSocketDeviceReader
import com.hpe.iot.dc.util.DataParserUtility
import com.hpe.iot.dc.util.UtilityLogger

/**
 * @author sveera
 *
 */

public class DrivatemateConstants{
	public static final int handShakeMsgLen=17;
	public static final String HAND_SHAKE = "HandShake"
	public static final String NOTIFICATION="notification"
}

public class MMIDrivemateServerSocketToDeviceModel implements ServerSocketToDeviceModel{

	public String getManufacturer(){
		return "MMI";
	}

	public String getModelId(){
		return "Drivemate";
	}

	public String getBoundLocalAddress(){
		return "10.3.239.75";
	}

	public int getPortNumber(){
		return 2004;
	}

	public String getDescription() {
		return "This is a OBD type vehicle tracking plug & sense connector.";
	}

	@Override
	public TCPOptions getTCPOptions() {
		return new TCPOptions(){
					@Override
					public ProcessingTaskType getProcessingTaskType() {
						return ProcessingTaskType.SOCKET_SESSION_BASED_DATA_PROCESSING;
					}

					@Override
					public int getBufferCapacity() {
						return 2048;
					}

					@Override
					public int getSocketBacklogCount() {
						return 10000;
					}
				};
	}
}


public class MMIDrivemateDeviceClientSocketExtractor implements DeviceClientSocketExtractor{

	private static final int GPS_PAYLOAD_LEN = 15;

	@Override
	public Device extractConnectedDevice(byte[] clientSocketData, SocketChannel socketChannel, DeviceModel deviceModel,
			ClientSocketDeviceReader clientSocketDeviceReader) {
		Device device=clientSocketDeviceReader.getDevice(socketChannel);
		return device!=null?device:extractDeviceFromPayload(clientSocketData,deviceModel);
	}

	private Device extractDeviceFromPayload(byte[] clientSocketData,DeviceModel deviceModel){
		UtilityLogger.logRawDataInDecimalFormat(clientSocketData,getClass());
		return clientSocketData!=null&&clientSocketData.length==DrivatemateConstants.handShakeMsgLen?
				new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(),
				DataParserUtility.convertBytesToASCIIString(clientSocketData,2,GPS_PAYLOAD_LEN)):null;
	}
}

public class MMIDrivemateDataModelTransformer extends SessionBasedUplinkDataModelTransformer {

	private final byte[] header=[0, 0, 0, 0] as byte[];
	private final UplinkDeviceDataConverter uplinkDeviceDataConverter;

	public MMIDrivemateDataModelTransformer(UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory) {
		super(uplinkDeviceDataConverterFactory);
		uplinkDeviceDataConverter=uplinkDeviceDataConverterFactory.getModelConverter(DrivatemateConstants.NOTIFICATION);
	}

	@Override
	protected List<DeviceInfo> convertToModelForDevice(Device device, byte[] input) {
		List<DeviceInfo> deviceInfo=new ArrayList<>();
		boolean isHandShakeMsgType=checkWhetherHandShakeMsgType(input);
		if(isHandShakeMsgType)
			deviceInfo.add(new DeviceInfo(device, DrivatemateConstants.HAND_SHAKE, input));
		else
			deviceInfo=handleForNonHandShakeMessageType(device,input);
		return deviceInfo;
	}

	private boolean checkWhetherHandShakeMsgType(byte[] input){
		return input!=null&&input.length==DrivatemateConstants.handShakeMsgLen?true:false;
	}

	private List<DeviceInfo> handleForNonHandShakeMessageType(Device device, byte[] input){
		List<DeviceInfo> deviceInfo=new ArrayList<>();
		int startingArrayIndex=0,endingArrayIndex;
		while(isInvalidData(input,startingArrayIndex)){
			endingArrayIndex=startingArrayIndex+12+Integer.parseInt(DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(
					Arrays.copyOfRange(input,startingArrayIndex+4,startingArrayIndex+8)));
			deviceInfo.add(uplinkDeviceDataConverter.createModel(device,Arrays.copyOfRange(input,startingArrayIndex,endingArrayIndex)));
			startingArrayIndex=endingArrayIndex;
		}
		return deviceInfo;
	}

	private boolean isInvalidData(byte[] input,int dataArrayIndex){
		return input.length<=(dataArrayIndex+8)||
				(input.length<dataArrayIndex+12+
				Integer.parseInt(DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(Arrays.copyOfRange(input,dataArrayIndex+4,dataArrayIndex+8))))?
				false:true;
	}
}


public class HandshakeMessageService implements MessageService{

	private final TCPServerSocketWriter tcpServerSocketWriter;

	private final byte[] ackMessage;

	public HandshakeMessageService(TCPServerSocketWriter tcpServerSocketWriter) {
		super();
		this.tcpServerSocketWriter = tcpServerSocketWriter;
		ackMessage=new byte[4];
		ackMessage[0]=1;
	}

	@Override
	public String getMessageType() {
		return DrivatemateConstants.HAND_SHAKE;
	}

	@Override
	public DeviceDataDeliveryStatus executeService(DeviceInfo deviceInfo) {
		tcpServerSocketWriter.sendMessage(deviceInfo.getDevice(),ackMessage);
		return new DeviceDataDeliveryStatus();
	}
}

public class GPSInfo{
	private final String gpsTimeStamp;
	private final int priority;
	private final String latitutde;
	private final String longitude;
	private final String altitude;
	private final String angle;
	private final int visibleSatillites;
	private final int speed;

	public GPSInfo(String gpsTimeStamp, int priority, String latitutde, String longitude, String altitude, String angle,
	int visibleSatillites, int speed) {
		super();
		this.gpsTimeStamp = gpsTimeStamp;
		this.priority = priority;
		this.latitutde = latitutde;
		this.longitude = longitude;
		this.altitude = altitude;
		this.angle = angle;
		this.visibleSatillites = visibleSatillites;
		this.speed = speed;
	}

	public String getGpsTimeStamp() {
		return gpsTimeStamp;
	}

	public int getPriority() {
		return priority;
	}

	public String getLatitutde() {
		return latitutde;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getAltitude() {
		return altitude;
	}

	public String getAngle() {
		return angle;
	}

	public int getVisibleSatillites() {
		return visibleSatillites;
	}

	public int getSpeed() {
		return speed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((altitude == null) ? 0 : altitude.hashCode());
		result = prime * result + ((angle == null) ? 0 : angle.hashCode());
		result = prime * result + ((gpsTimeStamp == null) ? 0 : gpsTimeStamp.hashCode());
		result = prime * result + ((latitutde == null) ? 0 : latitutde.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + priority;
		result = prime * result + speed;
		result = prime * result + visibleSatillites;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GPSInfo other = (GPSInfo) obj;
		if (altitude == null) {
			if (other.altitude != null)
				return false;
		} else if (!altitude.equals(other.altitude))
			return false;
		if (angle == null) {
			if (other.angle != null)
				return false;
		} else if (!angle.equals(other.angle))
			return false;
		if (gpsTimeStamp == null) {
			if (other.gpsTimeStamp != null)
				return false;
		} else if (!gpsTimeStamp.equals(other.gpsTimeStamp))
			return false;
		if (latitutde == null) {
			if (other.latitutde != null)
				return false;
		} else if (!latitutde.equals(other.latitutde))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (priority != other.priority)
			return false;
		if (speed != other.speed)
			return false;
		if (visibleSatillites != other.visibleSatillites)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GPSInfo [gpsTimeStamp=" + gpsTimeStamp + ", priority=" + priority + ", latitutde=" + latitutde+ ", longitude=" + longitude + ", altitude=" + altitude + ", angle=" + angle + ", visibleSatillites="+ visibleSatillites + ", speed=" + speed + "]";
	}
}

public class NotificationRecord {

	private final GPSInfo gpsInfo;
	private final String eventType;
	private final Map<String,String> events;

	public NotificationRecord(GPSInfo gpsInfo, String eventType, Map<String, String> events) {
		super();
		this.gpsInfo = gpsInfo;
		this.eventType = eventType;
		this.events = events;
	}

	public GPSInfo getGpsInfo() {
		return gpsInfo;
	}

	public String getEventType() {
		return eventType;
	}

	public Map<String, String> getEvents() {
		return events;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((events == null) ? 0 : events.hashCode());
		result = prime * result + ((gpsInfo == null) ? 0 : gpsInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationRecord other = (NotificationRecord) obj;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		if (gpsInfo == null) {
			if (other.gpsInfo != null)
				return false;
		} else if (!gpsInfo.equals(other.gpsInfo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NotificationRecord [gpsInfo=" + gpsInfo + ", eventType=" + eventType + ", events=" + events + "]";
	}
}

public class Notification implements DeviceData{

	private final int noOfRecords;
	private final List<NotificationRecord> notificationRecords;

	public Notification(int noOfRecords,List<NotificationRecord> notificationRecords) {
		super();
		this.notificationRecords = notificationRecords;
		this.noOfRecords=noOfRecords;
	}

	@Override
	public String getDeviceDataInformation() {
		return "notification";
	}

	public int getNoOfRecords() {
		return noOfRecords;
	}

	public List<NotificationRecord> getNotificationRecords() {
		return notificationRecords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + noOfRecords;
		result = prime * result + ((notificationRecords == null) ? 0 : notificationRecords.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (noOfRecords != other.noOfRecords)
			return false;
		if (notificationRecords == null) {
			if (other.notificationRecords != null)
				return false;
		} else if (!notificationRecords.equals(other.notificationRecords))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Notification [noOfRecords=" + noOfRecords + ", notificationRecords=" + notificationRecords + "]";
	}
}

public class EventIdToNameMapper{

	private final Map<Integer,String> eventIdMap=new HashMap<>();

	public EventIdToNameMapper(){
		initEventMap();
	}

	private void initEventMap(){
		eventIdMap.put(0,"Regular Update");
		eventIdMap.put(1,"Ignition");
		eventIdMap.put(181,"GPS PDOP");
		eventIdMap.put(182,"GPS HDOP");
		eventIdMap.put(66,"External Voltage");
		eventIdMap.put(240,"Movement");
		eventIdMap.put(199,"Trip Distance (Km)");
		eventIdMap.put(241,"Active GSM operator");
		eventIdMap.put(24,"GPS speed (km/h)");
		eventIdMap.put(80,"Data mode");
		eventIdMap.put(21,"GSM signal");
		eventIdMap.put(200,"Deep sleep");
		eventIdMap.put(205,"GSM cell ID");
		eventIdMap.put(206,"GSM area code");
		eventIdMap.put(67,"Battery voltage (mV)");
		eventIdMap.put(68,"Battery current (mA)");
		eventIdMap.put(16,"Total Distance (km)");
		eventIdMap.put(155,"Geofence zone 01");
		eventIdMap.put(156,"Geofence zone 02");
		eventIdMap.put(157,"Geofence zone 03");
		eventIdMap.put(158,"Geofence zone 04");
		eventIdMap.put(159,"Geofence zone 05");
		eventIdMap.put(175,"Auto Geofence");
		eventIdMap.put(250,"Trip");
		eventIdMap.put(255,"Over Speeding");
		eventIdMap.put(251,"Excessive Idling");
		eventIdMap.put(253,"Green driving type");
		eventIdMap.put(252,"Unplug detection");
		eventIdMap.put(247,"Crash detection");
		eventIdMap.put(248,"Alarm");
		eventIdMap.put(254,"Green driving value");
		eventIdMap.put(249,"Jamming");
	}

	public String getEventName(int eventId){
		return eventIdMap.get(eventId);
	}
}


public class UplinkNotificationMessageConverter extends SessionBasedUplinkDeviceDataConverter{

	private static final int GPS_PAYLOAD_LEN = 15
	private static final int TIMESTAMP_PAYLOAD_LEN = 8
	private static final int PRIORITY_BYTE = 1

	private final EventIdToNameMapper eventIdToNameMapper;

	public UplinkNotificationMessageConverter(EventIdToNameMapper eventIdToNameMapper){
		this.eventIdToNameMapper=eventIdToNameMapper;
	}

	@Override
	public String getMessageType() {
		return "notification";
	}

	protected DeviceInfo createModel(Device device, byte[] input){
		DeviceInfo deviceInfo=new DeviceInfo(device, messageType, input)
		Map<String, DeviceData> deviceData=deviceInfo.getDeviceData();
		fillDeviceData(deviceData,input);
		return deviceInfo;
	}

	private void fillDeviceData(Map<String, DeviceData> deviceData,byte[] input){
		int noOfRecords=input[9];
		List<NotificationRecord> notificationRecords=new ArrayList<>(noOfRecords);
		Notification  notification=new Notification(noOfRecords,notificationRecords);
		deviceData.put(notification.getDeviceDataInformation(),notification);
		if(noOfRecords==0)
			return;

		handleForNonZeroRecords(noOfRecords,notificationRecords,input);
	}

	private void handleForNonZeroRecords(int noOfRecords,List<NotificationRecord> notificationRecords,byte[] input){
		int startingIndexOfRecord=0,endingIndexOfRecord=0;
		for(int recordCounter=1;recordCounter<=noOfRecords;recordCounter++){
			if(startingIndexOfRecord==0)
				startingIndexOfRecord=10;
			endingIndexOfRecord=calculateEndingIndex(startingIndexOfRecord,input);
			notificationRecords.add(createNotificationRecord(Arrays.copyOfRange(input,startingIndexOfRecord,endingIndexOfRecord+1)));
			startingIndexOfRecord=endingIndexOfRecord+1;
		}
	}

	private int calculateEndingIndex(int startingIndexOfRecord,byte[] input){
		int noOfIOEventsIndex=startingIndexOfRecord+TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+GPS_PAYLOAD_LEN+1;
		int noOfIOEvents=input[noOfIOEventsIndex];
		if(noOfIOEvents==0)
			return noOfIOEventsIndex;
		int endingIndexOfRecord=noOfIOEventsIndex;
		for(int ioEventCounterType=1;ioEventCounterType<=8;ioEventCounterType=ioEventCounterType*2)
			endingIndexOfRecord=endingIndexOfRecord+1+((ioEventCounterType+1)*input[endingIndexOfRecord+1]);
		return endingIndexOfRecord;
	}

	private NotificationRecord createNotificationRecord(byte[] notificationRecordData){
		long timeStampEpochTime=Long.parseLong(calculateUnsignedDecimalValFromSignedBytes(Arrays.copyOf(notificationRecordData,TIMESTAMP_PAYLOAD_LEN)));
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
		String timeStamp = formatter.format(new Date(timeStampEpochTime));
		int priority=notificationRecordData[TIMESTAMP_PAYLOAD_LEN];
		String longitude=calculateUnsignedDecimalValFromSignedBytes(
				Arrays.copyOfRange(notificationRecordData,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+4));
		String latitude=calculateUnsignedDecimalValFromSignedBytes(
				Arrays.copyOfRange(notificationRecordData,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+4,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+8));
		String altitude=calculateUnsignedDecimalValFromSignedBytes(
				Arrays.copyOfRange(notificationRecordData,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+8,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+10));
		String angle=calculateUnsignedDecimalValFromSignedBytes(
				Arrays.copyOfRange(notificationRecordData,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+10,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+12));
		int noOfSatilites=notificationRecordData[TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+12];
		int speed=Integer.parseInt(calculateUnsignedDecimalValFromSignedBytes(
				Arrays.copyOfRange(notificationRecordData,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+13,TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+15)));
		int eventType=notificationRecordData[TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+15];
		int noOfevents=notificationRecordData[TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+16];
		GPSInfo gpsInfo=new GPSInfo(timeStamp, priority, latitude, longitude, altitude, angle, noOfSatilites, speed);
		Map<String,String> events=noOfevents==0?new HashMap<>():extractEventsFromPayload(notificationRecordData);
		return new NotificationRecord(gpsInfo, eventIdToNameMapper.getEventName(eventType), events);
	}

	private Map<String,String> extractEventsFromPayload(byte[] notificationRecordData){
		final Map<Integer,byte[][]> eventTypeArrays=new HashMap<>();
		int eventArrayStartingIndex=TIMESTAMP_PAYLOAD_LEN+PRIORITY_BYTE+17,eventArrayEndingIndex=0;
		for(int ioEventCounterType=1;ioEventCounterType<=8;ioEventCounterType=ioEventCounterType*2){
			eventArrayEndingIndex=(ioEventCounterType+1)*notificationRecordData[eventArrayStartingIndex];
			if(eventArrayEndingIndex!=0){
				eventArrayEndingIndex+=eventArrayStartingIndex+1;
				eventTypeArrays.put(ioEventCounterType,
						DataParserUtility.splitArray(Arrays.copyOfRange(notificationRecordData,eventArrayStartingIndex+1,eventArrayEndingIndex),ioEventCounterType+1));
				eventArrayStartingIndex=eventArrayEndingIndex;
			}else
				eventArrayStartingIndex++;
		}
		return extractEventsFromEventTypeArrays(eventTypeArrays);
	}

	private Map<String,String> extractEventsFromEventTypeArrays(final Map<Integer,byte[][]> eventTypeArrays){
		final Map<String,String> allEvents=new HashMap<>();
		for(Map.Entry<Integer, byte[][]> eventType:eventTypeArrays.entrySet())
			allEvents.putAll(extractEventsFromEventTypeArray(eventType.getKey(),eventType.getValue()));
		return allEvents;
	}

	private Map<String,String> extractEventsFromEventTypeArray(int eventType,byte[][] eventTypeArray){
		final Map<String,String> events=new HashMap<>();
		for(int arrayIndex=0;arrayIndex<eventTypeArray.length;arrayIndex++){
			byte[] eventArray=eventTypeArray[arrayIndex];
			String enventType=eventIdToNameMapper.getEventName(eventArray[0]);
			if(enventType?.trim())
				events.put(enventType,DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(Arrays.copyOfRange(eventArray,1,eventArray.length)));
		}
		return events;
	}
}

public class NotificationMessageService implements UplinkMessageService{
	
	private final Logger logger=LoggerFactory.getLogger(this.getClass());

	def notification = "notification";

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;
	private final TCPServerSocketWriter tcpServerSocketWriter;

	public NotificationMessageService(IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService,TCPServerSocketWriter tcpServerSocketWriter) {
		super();
		this.iotPublisherService = iotPublisherService;
		this.tcpServerSocketWriter=tcpServerSocketWriter;
	}

	@Override
	public String getMessageType() {
		return notification;
	}

	@Override
	public DeviceDataDeliveryStatus executeService(DeviceInfo deviceInfo) {
		DeviceDataDeliveryStatus deviceDataDeliveryStatus=iotPublisherService.receiveDataFromDevice(deviceInfo,getContainerName());
		int noOfRecords=((Notification)deviceInfo.getDeviceData().get(notification)).getNoOfRecords();
		byte[] acknowlegement=DataParserUtility.getByteArrayValueInBigEndian(noOfRecords,4);
		tcpServerSocketWriter.sendMessage(deviceInfo.getDevice(),acknowlegement);
		return deviceDataDeliveryStatus; 
	}

	@Override
	public String getContainerName() {
		return notification;
	}
}
