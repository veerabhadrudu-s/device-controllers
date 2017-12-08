/**
 * 
 */
package com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters;

import static com.hpe.iot.dc.util.DataParserUtility.convertBytesToASCIIString;
import static com.hpe.iot.dc.util.DataParserUtility.convertToHexValue;
import static com.hpe.iot.dc.util.DataParserUtility.truncateEmptyBytes;
import static java.lang.Long.parseLong;
import static java.util.Arrays.copyOfRange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;

/**
 * @author sveera
 *
 */
public class MMIClientMessageConsumer implements ClientMessageConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String ACK_MSG_TYPE = "0x8001";

	@Override
	public ClientDeviceData handleMessage(byte[] message) {
		return generateDeviceDataForMessageType(message);
	}

	private ClientDeviceData generateDeviceDataForMessageType(byte[] message) {
		ClientDeviceData clientDeviceData = null;
		if (message != null && message.length > 26
				&& ACK_MSG_TYPE.equals("0x" + convertToHexValue(message[25], message[26]))) {
			byte[] deviceIdInByte = copyOfRange(message, 5, 25);
			deviceIdInByte = truncateEmptyBytes(deviceIdInByte);
			clientDeviceData = new ClientDeviceData(
					parseLong(convertBytesToASCIIString(deviceIdInByte, 0, deviceIdInByte.length)), message,
					ACK_MSG_TYPE);
			logger.trace("Returning " + clientDeviceData + " for messageType " + ACK_MSG_TYPE);
		}

		return clientDeviceData;
	}

}
