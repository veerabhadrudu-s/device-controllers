/**
 * 
 */
package com.hpe.iot.http.steamcode.metering;

import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.STREAMCODE_VERSION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */
public class StreamCodeTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "2e3a3a40-80ea-11e7-9a7b-00259075adf2";

	@Test
	@DisplayName("test Process DevicePayload For Stream Code Script")
	public void testProcessDevicePayloadForStreamCodeScript() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = putUplinkMessages(STREAMCODE, STREAMCODE_MODEL,
				STREAMCODE_VERSION, createPayloadForPayloadForStreamCodeNotification());
		validateConsumedUplinkMessage(STREAMCODE, STREAMCODE_MODEL, STREAMCODE_VERSION, DEVICE_ID,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	private String createPayloadForPayloadForStreamCodeNotification() {
		return "{\"" + DEVICE_ID + "\": 285.0}";
	}

}
