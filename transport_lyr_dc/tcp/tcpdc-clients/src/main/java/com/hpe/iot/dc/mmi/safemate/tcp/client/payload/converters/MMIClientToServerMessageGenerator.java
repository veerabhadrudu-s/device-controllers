/**
 * 
 */
package com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters;

import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.copyOfRange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm;
import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class MMIClientToServerMessageGenerator implements ClientMessageGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String IP_CONNECT_MSG_TYPE = "0x4001";
	private static final String NOTIF_MSG_TYPE = "0x4206";
	private static final String SOS_MSG_TYPE = "0x4203";

	//@formatter:off
	/*			
		"0c", "02", "01", 			--date
		"0c", "15", "17", 			--time
		"24", "c9", "02", "fc", 	--latitude
		"2a", "ad", "10", "00", 	--longitude
		"ff", "28", 				--speed
		"00", "03",					--direction
		"00",						--flag
		"00","00", "00", "00",		--TSTATE
		"64",						--Remaining Battery.
		"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
		"fb", "61", "0f","a1",		-- GSM CELL CODE
		"50", "37", "31", "38", "5f", "53", "20", "43", "45", "20", "49", "4e", "46", "4f", "20", "56", "31","2e", "30", "2e", "31", "00", --Firmware version
		"50", "37", "31", "38", "5f", "48", "20", "56", "31", "2e", "30", "2e", "33", "00", --Hardware version				
	*/
	//@formatter:on	

	private static final String[] IPCONNECT_DATA_MESSAGE_HEX =  new String[] {"0c", "02", "01", "0c", "15", "17", "24",
			"c9", "02", "fc", "2a", "ad", "10", "00", "ff", "28", "00", "03", "00", "00", "00", "00", "00", "64", "00",
			"00", "00", "00", "00", "00", "00", "fb", "61", "0f", "a1", "50", "37", "31", "38", "5f", "53", "20", "43",
			"45", "20", "49", "4e", "46", "4f", "20", "56", "31", "2e", "30", "2e", "31", "00", "50", "37", "31", "38",
			"5f", "48", "20", "56", "31", "2e", "30", "2e", "33", "00"};

	//@formatter:off
	/*	
		"00"						--flag
		"01"						--pkg_item (No of GPS packets in this Message)
		"04","0c","10", 			--date
		"11","37","13", 			--time
		"98","26","c9","02",	 	--latitude
		"d0","2f","ad","10", 		--longitude
		"0d","00",	 				--speed
		"01","07",					--direction
		"67",						--flag
		"00","00", "00", "00",		--TSTATE
		"64",						--Remaining Battery.
		"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
		"fc","61","74","60",		-- GSM CELL CODE			
	*/
	//@formatter:on	

	public static final String[] NOTIFICATION_MESSAGE_HEX = new String[] { "00", "01", "04", "0c", "10", "11", "37",
			"13", "98", "26", "c9", "02", "d0", "2f", "ad", "10", "0d", "00", "01", "07", "67", "00", "00", "00", "00",
			"64", "00", "00", "00", "00", "00", "00", "00", "fc", "61", "74", "60"};
		
	//@formatter:off
	/*		
		"06", "0c", "10", 			--date
		"09", "22", "1b",			--time
		"4e", "ee", "c9", "02",	 	--latitude
		"3e", "8a", "ab", "10",		--longitude
		"00", "00",	 				--speed
		"00","00",					--direction
		"03",						--flag
		"40", "00", "00", "00",		--TSTATE
		"64",						--Remaining Battery.
		"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
		"fb", "61", "0f", "a1",		-- GSM CELL CODE
		"a9", "5c",					-- CRC
		"0d","0a" 					-- TAIL
	*/
	//@formatter:on

	public static final String[] SOS_MESSAGE_HEX =  new String[] { "06", "0c", "10", "09", "22", "1b", "4e", "ee", "c9", "02",
		 "3e", "8a", "ab", "10", "00", "00", "00","00", "03", "40", "00", "00", "00", "64", "00", "00", "00", 
		 "00", "00", "00", "00", "fb", "61", "0f", "a1"};

	private static final int HEAD = 2, LENGTH = 2, PROTO_VER = 1, DEVICE_ID = 20, MESSAGE_TYPE = 2, CRC = 2, TAIL = 2;

	private MMICRCAlgorithm mmicrcAlgorithm;

	public MMIClientToServerMessageGenerator(MMICRCAlgorithm mmicrcAlgorithm) {
		super();
		this.mmicrcAlgorithm = mmicrcAlgorithm;
	}

	@Override
	public ClientDeviceData generateMessagePacket(long deviceId, String messageType) {
		return generateDeviceDataForMessageType(deviceId, messageType);
	}

	private ClientDeviceData generateDeviceDataForMessageType(long deviceId, String messageType) {

		ClientDeviceData clientDeviceData = null;
		switch (messageType) {
		case IP_CONNECT_MSG_TYPE:
			clientDeviceData = new ClientDeviceData(deviceId,
					constructDeviceMessage(createBinaryPayloadFromHexaPayload(IPCONNECT_DATA_MESSAGE_HEX, getClass()),
							Long.toString(deviceId), IP_CONNECT_MSG_TYPE),
					messageType);
			break;
		case NOTIF_MSG_TYPE:
			clientDeviceData = new ClientDeviceData(deviceId,
					constructDeviceMessage(createBinaryPayloadFromHexaPayload(NOTIFICATION_MESSAGE_HEX, getClass()),
							Long.toString(deviceId), NOTIF_MSG_TYPE),
					messageType);
			break;
		case SOS_MSG_TYPE:
			clientDeviceData = new ClientDeviceData(deviceId,
					constructDeviceMessage(createBinaryPayloadFromHexaPayload(SOS_MESSAGE_HEX, getClass()),
							Long.toString(deviceId), SOS_MSG_TYPE),
					messageType);
			break;
		}
		logger.trace("Returning " + clientDeviceData + " for messageType " + messageType);
		return clientDeviceData;
	}

	protected byte[] constructDeviceMessage(byte[] dataPayload, String deviceId, String messageType) {
		int payloadLength = dataPayload == null ? 0 : dataPayload.length;
		int commandByteLength = HEAD + LENGTH + PROTO_VER + DEVICE_ID + MESSAGE_TYPE + payloadLength + CRC + TAIL;
		byte[] commandBytes = new byte[commandByteLength];
		fillHeader(commandBytes);
		fillLengthOfMessage(commandBytes);
		fillProtocolVersion(commandBytes);
		fillDeviceID(commandBytes, deviceId);
		fillProtocolType(commandBytes, messageType);
		fillProtocolData(commandBytes, dataPayload);
		fillCRC(commandBytes);
		fillTail(commandBytes);
		return commandBytes;
	}

	private void fillTail(byte[] commandBytes) {
		commandBytes[commandBytes.length - 2] = 13;
		commandBytes[commandBytes.length - 1] = 10;
	}

	private void fillCRC(byte[] commandBytes) {
		byte[] dataWithOutCRC = copyOfRange(commandBytes, 0, commandBytes.length - 4);
		String crc = mmicrcAlgorithm.calculateCRC(dataWithOutCRC);
		String leftHexVal = crc.substring(0, 2);
		String rightHexVal = crc.substring(2, crc.length());
		commandBytes[commandBytes.length - 4] = (byte) parseInt(leftHexVal, 16);
		commandBytes[commandBytes.length - 3] = (byte) parseInt(rightHexVal, 16);
	}

	private void fillProtocolData(byte[] commandBytes, byte[] dataPayload) {
		if (dataPayload == null)
			return;
		for (int deviceIdByteIndex = 0; deviceIdByteIndex < dataPayload.length; deviceIdByteIndex++)
			commandBytes[deviceIdByteIndex + 27] = dataPayload[deviceIdByteIndex];
	}

	private void fillProtocolType(byte[] commandBytes, String messageType) {
		commandBytes[25] = (byte) parseInt(messageType.substring(2, 4), 16);
		commandBytes[26] = (byte) parseInt(messageType.substring(4, 6), 16);
	}

	private void fillDeviceID(byte[] commandBytes, String deviceId) {
		byte[] deviceIdBytes = deviceId.getBytes();
		if (deviceIdBytes.length > 20)
			throw new InvalidDeviceId("Device ID length is more than 20 characters");
		for (int deviceIdByteIndex = 0; deviceIdByteIndex < deviceIdBytes.length; deviceIdByteIndex++)
			commandBytes[deviceIdByteIndex + 5] = deviceIdBytes[deviceIdByteIndex];
	}

	private void fillProtocolVersion(byte[] commandBytes) {
		commandBytes[4] = 1;
	}

	private void fillLengthOfMessage(byte[] commandBytes) {
		if (commandBytes.length < 255)
			commandBytes[2] = (byte) (commandBytes.length);
		else {
			// TODO: Logic for splitting length into Two bytes.
		}
	}

	private void fillHeader(byte[] commandBytes) {
		commandBytes[0] = 64;
		commandBytes[1] = 64;
	}

	public class InvalidDeviceId extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public InvalidDeviceId(String message) {
			super(message);
		}
	}

}
