/**
 * 
 */
package com.hpe.iot.http.libelium.smartagriculture;

import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM;
import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.LIBELIUM_VERSION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

/**
 * @author sveera
 *
 */
public class LibeliumSmartAgricultureProTest extends HttpPluginTestBaseTemplate {

	private static final String DEVICE_ID = "0102030405060708";

	@Test
	@DisplayName("test Process DevicePayload For Libelium Smart AgriculturePro Notification Msg Typ")
	public void testHttpSouthboundServiceForLibeliumSmartAgricultureProForNotificationMsgTyp() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = putUplinkMessages(LIBELIUM, LIBELIUM_MODEL, LIBELIUM_VERSION,
				getLibeliumSmartAgricultureProForNotificationMsgTyp());
		validateConsumedUplinkMessage(LIBELIUM, LIBELIUM_MODEL, LIBELIUM_VERSION, DEVICE_ID,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	private String getLibeliumSmartAgricultureProForNotificationMsgTyp() {
		return "{\"cmd\":\"rx\",\"seqno\":556,\"EUI\":\"" + DEVICE_ID
				+ "\",\"ts\":1505749821468,\"fcnt\":6,\"port\":3,\"freq\":868300000,\"rssi\":-72,\"snr\":9.8,\"toa\":179,\"dr\":\"SF7 BW125 4/5\",\"ack\":false,\"bat\":255,\"data\":\"493a343038353139383136234e3a36234241543a34392354533a4d6f6e2c2031332f30352f30372c2030303a32343a3130235443423a32362e31362348554d423a36382e373923534f494c543a32362e3138234c573a302e30300d0a\"}";

	}

}
