/**
 * 
 */
package com.hpe.iot.http.gaia.smartwater.v1;

import static com.handson.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload;
import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static com.hpe.iot.http.test.constants.TestConstants.GAIA;
import static com.hpe.iot.http.test.constants.TestConstants.GAIA_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.GAIA_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */
public class GaiaSmartWaterTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "29865667679223848";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	@DisplayName("test Process DevicePayload For Gaia Script")
	public void testUplinkNotification() throws Exception {
		logger.trace("Uplink data used for posting is " + convertArrayOfByteToString(getUplinkNotficationData()));
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(GAIA, GAIA_MODEL, GAIA_VERSION, 1,
				getUplinkNotficationData());
		validateConsumedUplinkMessage(GAIA, GAIA_MODEL, GAIA_VERSION, DEVICE_ID,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
		validatePayload(iotDevicePayloadHolder.getIOTDeviceData().get(0).getPayload());
	}

	private void validatePayload(JsonObject jsonObject) {
		assertNotNull(jsonObject, "Device payload cannot be null");
		assertEquals(DEVICE_ID, jsonObject.get("gaiaId").getAsString(), "Device payload cannot be null");
		assertEquals("5013", jsonObject.get("seqeunceNumber").getAsString(), "Device payload cannot be null");
		assertEquals("4", jsonObject.get("forwardTotalizerInLts").getAsString(), "Device payload cannot be null");
		assertEquals("0", jsonObject.get("reverseTotalizerInLts").getAsString(), "Device payload cannot be null");
		assertEquals("4", jsonObject.get("totalTotalizerInLts").getAsString(), "Device payload cannot be null");
		assertEquals("0", jsonObject.get("flag1").getAsString(), "Device payload cannot be null");
		assertEquals("0", jsonObject.get("flag2").getAsString(), "Device payload cannot be null");
	}

	private byte[] getUplinkNotficationData() {
		String[] uplinkBinary26ByteData = new String[] { "00", "6A", "1A", "AA", "A1", "00", "00", "28", "00", "00",
				"13", "95", "00", "00", "00", "04", "00", "00", "00", "00", "00", "00", "00", "04", "00", "00" };
		return createBinaryPayloadFromHexaPayload(uplinkBinary26ByteData, getClass());
	}

}
