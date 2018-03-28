package com.handson.iot.dc.util;

import static com.handson.iot.dc.util.DataParserUtility.convertToHexValue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author sveera
 *
 */
public final class UtilityLogger {

	public static void logRawDataInHexaDecimalFormat(final byte[] input, Class<?> classType) {
		getLogger(classType)
				.info("Raw data from input channel in hexa format is " + convertArrayOfByteToHexString(input));
	}

	public static String convertArrayOfByteToHexString(final byte[] input) {
		String datahexString = "";
		for (byte rawbyte : input)
			datahexString += convertToHexValue(rawbyte) + " ";
		return datahexString;
	}

	public static void logRawDataInDecimalFormat(final byte[] input, Class<?> classType) {
		getLogger(classType)
				.info("Raw data from input channel in decimal format is " + convertArrayOfByteToString(input));
	}

	public static String convertArrayOfByteToString(final byte[] input) {
		String dataString = "";
		for (byte rawbyte : input)
			dataString += rawbyte + " ";
		return dataString;
	}

	public static void logExceptionStackTrace(Throwable ex, Class<?> classType) {
		getLogger(classType).error(exceptionStackToString(ex));
	}

	public static String exceptionStackToString(Throwable ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	private UtilityLogger() {
	}

}
