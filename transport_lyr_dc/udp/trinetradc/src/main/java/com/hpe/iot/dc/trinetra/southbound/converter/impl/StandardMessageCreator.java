package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.hpe.iot.dc.trinetra.model.NotificationRecord;
import com.hpe.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
@Component
public class StandardMessageCreator {

	public NotificationRecord constructStandardMessage(byte[] input) {
		String odometerReading = DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(input[8], input[7],
				input[6]);
		String latitude = calculateLatituteInforamtion(input[11], input[12], input[13], input[14]);
		String longitude = calculateLongitudeInforamtion(input[14], input[15], input[16], input[17]);
		String date = calculateDateInforamtion(input[24], input[25]);
		String time = calculateTimeInforamtion(input[21], input[22], input[23]);
		NotificationRecord notificationRecord=new NotificationRecord(latitude, longitude, date, time, odometerReading);
		Map<String,Object> customInfo=notificationRecord.getCustomInfo();
		addCustomInfo(customInfo,input);		
		return notificationRecord;

	}


	private String calculateTimeInforamtion(byte byte25, byte byte26, byte byte27) {
		int hrs = Integer.parseInt(DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(byte25));
		int mnts = Integer.parseInt(DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(byte26));
		int scnds = Integer.parseInt(DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(byte27));
		return String.format("%02d:%02d:%02d", hrs, mnts, scnds);
	}

	private String calculateDateInforamtion(byte byte28, byte byte29) {
		byte date = (byte) (byte29 >>> 3);
		date &= 0X1F;
		byte centuryValue = (byte) (byte28 & 0X7F);
		int year = centuryValue + 2000;
		byte month = (byte) (byte29 & 0X07);
		month <<= 1;
		month |= ((byte28 >>> 7) & 0X01);
		return String.format("%02d/%02d/%04d", month, date, year);
	}

	private String calculateLatituteInforamtion(byte byte15, byte byte16, byte byte17, byte byte18) {
		byte18 &= (byte) 7;
		return DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(byte18, byte17, byte16, byte15).concat("N");
	}

	private String calculateLongitudeInforamtion(byte byte18, byte byte19, byte byte20, byte byte21) {
		byte18 = (byte) (byte18 >>> 3);
		byte18 &= 0X1F;
		return DataParserUtility.calculateUnsignedDecimalValFromSignedBytes(byte21, byte20, byte19, byte18).concat("W");
	}
	
	private void addCustomInfo(Map<String, Object> customInfo, byte[] input) {
		addSensorInformation(customInfo, input);
	}

	private void addSensorInformation(Map<String, Object> customInfo, byte[] input) {
		byte sensorStatusByte = input[5];
		byte sensorType = (byte) (sensorStatusByte & 0X1F);
		int sensorStatus = (((byte) (sensorStatusByte & 0X20)) >>> 5);
		if (sensorType == 2) {
			customInfo.put("Ignition", sensorStatus);
		}
	}

}
