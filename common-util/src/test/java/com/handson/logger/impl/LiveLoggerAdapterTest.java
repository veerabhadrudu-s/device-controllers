/**
 * 
 */
package com.handson.logger.impl;

import static com.handson.logger.constants.DeviceModelConstants.DEVICE_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.handson.logger.LiveLogger;
import com.handson.logger.constants.DeviceModelConstants;
import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class LiveLoggerAdapterTest {

	private LiveLogger liveLogger;
	private LoggerServiceSpy loggerServiceSpy = new LoggerServiceSpy();

	@BeforeEach
	void setUp() throws Exception {
		liveLogger = new LiveLoggerAdapter(loggerServiceSpy, DEVICE_MODEL);
	}

	@Test
	@DisplayName("check is liveLogger Null")
	public void testNotNull() {
		assertNotNull(liveLogger, "Live Logger cannot be null");
	}

	@Test
	@DisplayName("test liveLogger with null input")
	public void testLoggerWithNull_ExpectatingNoNullPointerException() {
		liveLogger.log(null);
	}

	@Test
	@DisplayName("test liveLogger with String data")
	public void testLoggerWithStringData() {
		String message = "test log message";
		liveLogger.log(message);
		assertLoggedMessages(message, DEVICE_MODEL);
	}

	@Test
	@DisplayName("test liveLogger with Device Data Type data")
	public void testLoggerWithDeviceDataType() {
		Device device = new DeviceImpl(DeviceModelConstants.MANUFACTURER, DeviceModelConstants.MODEL_ID,
				DeviceModelConstants.VERSION, "10000");
		liveLogger.log(device);
		assertLoggedMessages(device.toString(), DEVICE_MODEL);
	}

	@Test
	@DisplayName("test liveLogger with Integer Wrapper Type data")
	public void testLoggerWithWrapperDataType() {
		int logMessage = 1000;
		liveLogger.log(new Integer(logMessage));
		assertLoggedMessages(new Integer(logMessage).toString(), DEVICE_MODEL);
	}

	private void assertLoggedMessages(String expectedLoggedMessage, DeviceModel expectedDeviceModel) {
		assertEquals(expectedLoggedMessage, loggerServiceSpy.getLoggedMessage(),
				"Expected and actual logged messages are not same");
		assertEquals(expectedDeviceModel, loggerServiceSpy.getDeviceModel(),
				"Expected and actual device models are not same");
	}

	private class LoggerServiceSpy implements LoggerService {
		private String loggedMessage;
		private DeviceModel deviceModel;

		public String getLoggedMessage() {
			return loggedMessage;
		}

		public DeviceModel getDeviceModel() {
			return deviceModel;
		}

		@Override
		public void log(DeviceModel deviceModel, String message) {
			this.deviceModel = deviceModel;
			this.loggedMessage = message;
		}
	}
}
