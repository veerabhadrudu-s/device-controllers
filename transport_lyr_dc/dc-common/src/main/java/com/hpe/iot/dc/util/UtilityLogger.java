package com.hpe.iot.dc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sveera
 *
 */
public final class UtilityLogger {

	public static void logRawDataInHexaDecimalFormat(final byte[] input, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		String datahexString = "";
		for (byte rawbyte : input) {
			datahexString += DataParserUtility.convertToHexValue(rawbyte) + " ";
		}
		logger.info("Raw data from input channel in hexa format is " + datahexString);
	}

	public static void logRawDataInDecimalFormat(final byte[] input, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		String dataString = convertArrayOfByteToString(input);
		logger.info("Raw data from input channel in decimal format is " + dataString);
	}

	public static String convertArrayOfByteToString(final byte[] input) {
		String dataString = "";
		for (byte rawbyte : input) {
			dataString += rawbyte + " ";
		}
		return dataString;
	}

	public static String convertArrayOfByteToHexString(final byte[] input) {
		String datahexString = "";
		for (byte rawbyte : input) {
			datahexString += DataParserUtility.convertToHexValue(rawbyte) + " ";
		}
		return datahexString;
	}

	public static void logExceptionStackTrace(Throwable ex, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error(errors.toString());
	}

	public static String exceptionStackToString(Throwable ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	private UtilityLogger() {
	}

}
