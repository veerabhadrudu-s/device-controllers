/**
 * 
 */
package com.hpe.iot.test.constants;

import static java.io.File.separator;

/**
 * @author sveera
 *
 */
public class TestConstants {
	private static final String RESOURCE_FILE_PATH = "src" + separator + "test" + separator + "resources" + separator;
	public static final String DEVICE_MODELS_FULL_PATH = RESOURCE_FILE_PATH + "devicemodels.xml";
	public static final String EMPTY_DEVICE_MODELS_FULL_PATH = RESOURCE_FILE_PATH + "empty_devicemodels.xml";
	public static final String TRACKIMO = "Trackimo";
	public static final String TRACKIMO_MODEL = "TRKM002";
	public static final String TRACKIMO_VERSION = "1.0";
	public static final String TRACKIMO_DEV_ID_JPATH = "$.device_id";
	public static final String TRACKIMO_MSG_TYP_JPATH = "$.alarm_type";
	public static final String SAMPLE = "Sample";
	public static final String SAMPLE_1 = "Sample_1";
	public static final String SAMPLE_2 = "Sample_2";
	public static final String SAMPLE_MODEL = "SampleModel";
	public static final String SAMPLE_VERSION = "1.0";
	public static final String SAMPLE_DEV_ID_JPATH = "$.sample_id";
	public static final String SAMPLE_MSG_TYP_JPATH = "$.message_type";
	public static final String REXAWARE = "rexaware";
	public static final String REXAWARE_MODEL = "bikeevent";
	public static final String REXAWARE_VERSION = "1.0";
	public static final String REXAWARE_DEV_ID_JPATH = "$.DeviceData[0].DeviceID";
}
