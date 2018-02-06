package com.handson.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sveera
 *
 */
public final class UtilityLogger {
	

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
