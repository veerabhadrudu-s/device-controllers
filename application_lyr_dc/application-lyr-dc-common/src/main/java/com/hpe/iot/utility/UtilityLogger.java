package com.hpe.iot.utility;

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
		logger.info("Raw data in hexa format is " + datahexString);
	}

	public static void logRawDataInDecimalFormat(final byte[] input, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		String dataString = convertArrayOfByteToString(input);
		logger.info("Raw data in decimal format is " + dataString);
	}

	public static String convertArrayOfByteToString(final byte[] input) {
		String dataString = "";
		for (byte rawbyte : input) {
			dataString += rawbyte + " ";
		}
		return dataString;
	}

	public static void logExceptionStackTrace(Throwable ex, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error(errors.toString());
	}

	public static String toStringExceptionStackTrace(Throwable ex, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error(errors.toString());
		return errors.toString();
	}

	private UtilityLogger() {
	}

}
