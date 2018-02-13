package com.hpe.iot.dc.trinetra.southbound.transformer.impl;

import static com.handson.iot.dc.util.DataParserUtility.convertBytesToASCIIString;
import static com.handson.iot.dc.util.DataParserUtility.findAllClosingMessageByteIndexes;
import static com.handson.iot.dc.util.DataParserUtility.isMessageHasStartingBytes;
import static com.handson.iot.dc.util.DataParserUtility.truncateEmptyBytes;
import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static com.handson.iot.dc.util.UtilityLogger.logRawDataInDecimalFormat;
import static com.handson.iot.dc.util.UtilityLogger.logRawDataInHexaDecimalFormat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;

/**
 * @author sveera
 *
 */
@Component("dataModelTransformer")
public class TrinetraDataModelTransformer implements UplinkDataModelTransformer {

	private static final byte[] MESSAGE_STARTING_BYTES = new byte[] { 60 };

	private static final byte[] MESSAGE_CLOSING_BYTES = new byte[] { 62 };

	private final Logger logger = getLogger(this.getClass());

	private UplinkDeviceDataConverterFactory metaModelFactory;

	@Autowired
	public TrinetraDataModelTransformer(UplinkDeviceDataConverterFactory metaModelFactory) {
		super();
		this.metaModelFactory = metaModelFactory;
	}

	@Override
	public List<DeviceInfo> convertToModel(DeviceModel deviceModel, final byte[] input) {
		checkIsEmptyMessage(input);
		byte[] messageBytes = truncateEmptyBytes(input);
		logRawDataInDecimalFormat(messageBytes, this.getClass());
		logRawDataInHexaDecimalFormat(messageBytes, this.getClass());
		return createDataFramesFromInputBytes(deviceModel, messageBytes);
	}

	private List<DeviceInfo> createDataFramesFromInputBytes(DeviceModel deviceModel, byte[] input) {
		List<Integer> closingByteIndexs = findAllClosingMessageByteIndexes(input, MESSAGE_CLOSING_BYTES);
		List<byte[]> dataFrames = createDataFrames(input, closingByteIndexs);
		List<byte[]> validDataFrames = filterInvalidDataFrames(dataFrames);
		logger.debug("Extracted valid Data Frames length is " + validDataFrames.size());
		return constructDataModelsFromFrames(deviceModel, validDataFrames);
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
		boolean isValid = isMessageHasStartingBytes(dataFrame, MESSAGE_STARTING_BYTES);
		if (!isValid)
			logger.warn("Ignored Data Frame due to missing of Starting Byte '<' : "
					+ convertArrayOfByteToString(dataFrame));
		return isValid;
	}

	private void checkIsEmptyMessage(byte[] input) {
		if (input == null || input.length == 0) {
			throw new EmptyUDPMessage();
		}
	}

	private List<DeviceInfo> constructDataModelsFromFrames(DeviceModel deviceModel, List<byte[]> dataFrames) {
		List<DeviceInfo> dataModels = new ArrayList<>();
		for (byte[] dataFrame : dataFrames) {
			DeviceInfo dataModel = convertDataFrameToDataModel(deviceModel, dataFrame);
			if (dataModel != null)
				dataModels.add(dataModel);
		}
		return dataModels;
	}

	private DeviceInfo convertDataFrameToDataModel(DeviceModel deviceModel, byte[] input) {
		final String messageType = findMessageTypeFromInput(input);
		logger.debug("Identified Message Type from Data frame is " + messageType);
		UplinkDeviceDataConverter metaModelConverter = metaModelFactory.getModelConverter(messageType);
		logForInvalidMessageType(metaModelConverter, messageType);
		return metaModelConverter != null ? metaModelConverter.createModel(deviceModel, input) : null;
	}

	private void logForInvalidMessageType(UplinkDeviceDataConverter metaModelConverter, String messageType) {
		if (metaModelConverter == null)
			logger.warn("Invalid Message Type identified in Data Frame : " + messageType);
	}

	private String findMessageTypeFromInput(byte[] input) {
		return isMessageHasASCIIEncodedDeviceId(input) ? convertBytesToASCIIString(input, 7, 1)
				: convertBytesToASCIIString(input, 4, 1);
	}

	private boolean isMessageHasASCIIEncodedDeviceId(byte[] input) {
		final String deviceId = convertBytesToASCIIString(input, 1, 6);
		final String regexForNumeric = "\\d+";
		return deviceId.matches(regexForNumeric);
	}

}
