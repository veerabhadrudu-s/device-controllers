package com.hpe.iot.dc.valid

import static com.hpe.iot.dc.util.DataParserUtility.convertBytesToASCIIString
import static com.hpe.iot.dc.util.DataParserUtility.truncateEmptyBytes
import static java.util.Arrays.copyOfRange

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.dc.northbound.converter.inflow.impl.AbstractIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.southbound.service.inflow.UplinkMessageService
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer
import com.hpe.iot.dc.tcp.southbound.model.AbstractServerSocketToDeviceModel
import com.hpe.iot.dc.util.DataParserUtility
import com.hpe.iot.dc.util.UtilityLogger

/**
 * @author sveera
 *
 */
class SampleServerSocketToDeviceModel  extends AbstractServerSocketToDeviceModel {

	@Override
	public String getManufacturer() {
		return "Testing";
	}

	@Override
	public String getModelId() {
		return "Sample";
	}

	@Override
	public String getBoundLocalAddress() {
		return "localhost";
	}

	@Override
	public int getPortNumber() {
		return 2003;
	}
}

class SampleDataModelTransformer implements UplinkDataModelTransformer{
	private static def MESSAGE_STARTING_BYTES = [60] as byte[];
	private static def MESSAGE_CLOSING_BYTES = [62] as byte[];
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<DeviceInfo> convertToModel(DeviceModel deviceModel,byte[] input) {
		byte[] messageBytes = DataParserUtility.truncateEmptyBytes(input);
		UtilityLogger.logRawDataInDecimalFormat(messageBytes, getClass());
		UtilityLogger.logRawDataInHexaDecimalFormat(messageBytes, getClass());
		return createDataFramesFromInputBytes(deviceModel,messageBytes);
	}

	private List<DeviceInfo> createDataFramesFromInputBytes(DeviceModel deviceModel,byte[] input) {
		List<Integer> closingByteIndexs = DataParserUtility.findAllClosingMessageByteIndexes(input,
				MESSAGE_CLOSING_BYTES);
		List<byte[]> dataFrames = createDataFrames(input, closingByteIndexs);
		List<byte[]> validDataFrames = filterInvalidDataFrames(dataFrames);
		logger.debug("Extracted valid Data Frames length is " + validDataFrames.size());
		return constructDataModelsFromFrames(deviceModel,validDataFrames);
	}

	private List<byte[]> createDataFrames(byte[] input, List<Integer> closingByteIndexs) {
		List<byte[]> dataFrames = new ArrayList<>();
		int startIndex = 0;
		for (Integer closingFrameByte : closingByteIndexs) {
			dataFrames.add(Arrays.copyOfRange(input, startIndex, closingFrameByte + 1));
			startIndex = closingFrameByte + 1;
		}
		return dataFrames;
	}

	private List<byte[]> filterInvalidDataFrames(List<byte[]> dataFrames) {
		List<byte[]> validDataFrames = new ArrayList<>();
		for (byte[] dataFrame : dataFrames)
			if (isMessageStartingWithStartingByte(dataFrame))
				validDataFrames.add(dataFrame);
		return validDataFrames;
	}

	private boolean isMessageStartingWithStartingByte(byte[] dataFrame) {
		boolean isValid = DataParserUtility.isMessageHasStartingBytes(dataFrame, MESSAGE_STARTING_BYTES);
		if (!isValid)
			logger.warn("Ignored Data Frame due to missing of Starting Bytes "+MESSAGE_STARTING_BYTES+" : "
					+ UtilityLogger.convertArrayOfByteToString(dataFrame));
		return isValid;
	}

	private List<DeviceInfo> constructDataModelsFromFrames(DeviceModel deviceModel,List<byte[]> dataFrames) {
		List<DeviceInfo> dataModels = new ArrayList<>();
		for (byte[] dataFrame : dataFrames) {
			DeviceInfo dataModel = convertDataFrameToDataModel(deviceModel,dataFrame);
			if (dataModel != null)
				dataModels.add(dataModel);
		}
		return dataModels;
	}

	private DeviceInfo convertDataFrameToDataModel(DeviceModel deviceModel,byte[] input) {
		def deviceIdInByte = copyOfRange(input, 1, 11);
		deviceIdInByte = truncateEmptyBytes(deviceIdInByte);
		final String deviceId = convertBytesToASCIIString(deviceIdInByte, 0, deviceIdInByte.length);
		DeviceInfo deviceData=new DeviceInfo(new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceId), "deviceDataMessageType", input);
		SampleData sampleData=new SampleData(new String(copyOfRange(input,12,input.length-2)));
		deviceData.addDeviceData(sampleData.getDeviceDataInformation(),sampleData);
		return deviceData;
	}
}

public class SampleData implements DeviceData {

	public static final String SAMPLE_INFO = "Device data";

	public final String deviceData;


	public SampleData(String deviceData) {
		super();
		this.deviceData = deviceData;
	}

	public String getDeviceData() {
		return deviceData;
	}

	@Override
	public String toString() {
		return "SampleData [deviceData=" + deviceData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceData == null) ? 0 : deviceData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleData other = (SampleData) obj;
		if (deviceData == null) {
			if (other.deviceData != null)
				return false;
		} else if (!deviceData.equals(other.deviceData))
			return false;
		return true;
	}

	@Override
	public String getDeviceDataInformation() {
		return SAMPLE_INFO;
	}
}


class SampleDataService implements UplinkMessageService {

	private static final String MESSAGE_TYPE = "deviceDataMessageType";

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;

	public SampleDataService(IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService) {
		super();
		this.iotPublisherService = iotPublisherService;
	}

	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public DeviceDataDeliveryStatus executeService(DeviceInfo deviceInfo) {
		iotPublisherService.receiveDataFromDevice(deviceInfo, getContainerName());
		return new DeviceDataDeliveryStatus();
	}

	@Override
	public String getContainerName() {
		return "default";
	}
}

class SampleIOTModelConverter extends AbstractIOTModelConverterImpl{

	@Override
	public String getDeviceUniqueIDName() {
		return "SampleDevId";
	}
}
