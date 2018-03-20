/**
 * 
 */
package com.hpe.iot.http.avanseus.videoanalytics;

import static com.hpe.iot.http.test.constants.TestConstants.AVANSEUS;
import static com.hpe.iot.http.test.constants.TestConstants.AVANSEUS_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.AVANSEUS_VERSION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */

public class AvanseusTest extends HttpPluginTestBaseTemplate {

	private static final String CUSTOMER_ID = "idsuser";
	private static final String DEVICE_NAME = "Camera1";
	private static final String DEVICE_ID = CUSTOMER_ID + "&" + DEVICE_NAME;

	@Test
	@DisplayName("test Process DevicePayload For Avanseus Script")
	public void testProcessDevicePayloadForAvanseusScript() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(AVANSEUS, AVANSEUS_MODEL, AVANSEUS_VERSION,
				createPayloadForPayloadForAvanseusNotification());
		validateConsumedUplinkMessage(AVANSEUS, AVANSEUS_MODEL, AVANSEUS_VERSION, DEVICE_ID,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	private String createPayloadForPayloadForAvanseusNotification() {
		return "{\"event\":{\"eventID\":\"595e3afdacccc36b0db916789\","
				+ "\"eventType\":\"alert\",\"eventTime\":\"2017-04-19T13:22:41.121+0000\",\"eventMessage\":\"Dearmember1,Intrusionhasbeennoticedatthepremisesofidsuser.Viewclipathttp://ar34et\"},"
				+ "\"source\":{\"customerID\":\"" + CUSTOMER_ID + "\",\"deviceName\":\"" + DEVICE_NAME
				+ "\",\"IPAddress\":\"10.2.2.201\"},\"additionalInformation\":{\"receivers\":[{\"name\":\"member1\",\"phone\":[\"9620172550\",\"8951432882\",\"9880275453\",\"9811619401\"],"
				+ "\"emailID\":[\"abc@gmail.com\",\"myuser@gmail.com\",\"idsuser@gmail.com\"]},{\"name\":\"member2\",\"phone\":[\"7620182550\",\"9841532882\",\"8880275453\",\"8876619401\"],"
				+ "\"emailID\":[\"xyz@gmail.com\",\"user@gmail.com\",\"abc@gmail.com\"]}],\"whitelistedUser\":[{\"name\":\"raj\"},{\"name\":\"madan\"}],\"videoURL\":\"http://10.2.2.69/INT_VIdeos/123456.mp4\"}}";
	}

}
