/**
 * 
 */
package com.hpe.iot.dc.udp.utility;

import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sveera
 *
 */
public class TestUtility {

	private TestUtility() {
	}

	public static byte[] createDataWithPaddingBytes(Class<?> classtype, String[]... dataFrameStrings) {
		byte[] input = new byte[3072];
		List<byte[]> dataFrames = new ArrayList<>();
		for (String[] dataFrameString : dataFrameStrings)
			dataFrames.add(createDecimalPayloadFromHexaPayload(dataFrameString, classtype));
		int inputDataFrameIndex = 0;
		for (byte[] dataFrameBytes : dataFrames)
			for (int frameByteIndex = 0; frameByteIndex < dataFrameBytes.length; frameByteIndex++) {
				input[inputDataFrameIndex] = dataFrameBytes[frameByteIndex];
				inputDataFrameIndex++;
			}
		return input;
	}

	public static byte[] createDataWithPaddingBytes(Class<?> classtype, String[] dataFrameString) {
		byte[] input = new byte[3072];
		List<byte[]> dataFrames = new ArrayList<>();
		dataFrames.add(createDecimalPayloadFromHexaPayload(dataFrameString, classtype));
		int inputDataFrameIndex = 0;
		for (byte[] dataFrameBytes : dataFrames)
			for (int frameByteIndex = 0; frameByteIndex < dataFrameBytes.length; frameByteIndex++) {
				input[inputDataFrameIndex] = dataFrameBytes[frameByteIndex];
				inputDataFrameIndex++;
			}
		return input;
	}

}
