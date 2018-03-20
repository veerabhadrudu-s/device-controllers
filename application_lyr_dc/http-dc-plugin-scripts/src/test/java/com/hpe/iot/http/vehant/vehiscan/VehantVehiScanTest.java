package com.hpe.iot.http.vehant.vehiscan;

import static com.hpe.iot.http.test.constants.TestConstants.VEHANT;
import static com.hpe.iot.http.test.constants.TestConstants.VEHANT_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.VEHANT_VERSION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.http.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.http.test.base.HttpPluginTestBaseTemplate;

public class VehantVehiScanTest extends HttpPluginTestBaseTemplate {

	@Test
	@DisplayName("test Publish Uplink Notification Scanned By One Camera")
	public void testUplinkNotificationScannedByOneCamera() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(VEHANT, VEHANT_MODEL, VEHANT_VERSION,
				getVehantVehiScanNotficationPayloadCapturedByOneCamera());
		validateConsumedUplinkMessage(VEHANT, VEHANT_MODEL, VEHANT_VERSION, "Lane1",
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
	}

	@Test
	@DisplayName("test Publish Uplink Notification Scanned By Mutliple Cameras")
	public void testUplinkNotificationScannedByMukltipleCamera() throws Exception {
		IOTDevicePayloadHolder iotDevicePayloadHolder = postUplinkMessages(VEHANT, VEHANT_MODEL, VEHANT_VERSION, 2,
				getVehantVehiScanNotficationPayloadCapturedByMultipleCameras());
		validateConsumedUplinkMessageDeviceModel(VEHANT, VEHANT_MODEL, VEHANT_VERSION,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));
		validateConsumedUplinkMessageDeviceModel(VEHANT, VEHANT_MODEL, VEHANT_VERSION,
				iotDevicePayloadHolder.getIOTDeviceData().get(0));

	}

	private String getVehantVehiScanNotficationPayloadCapturedByOneCamera() {
		return "{\"Vehicle\":[{\"LicenseNum\":\"AP13X1234\",\"LPCategory\":\"Stolen\",\"Time\":\"2017-05-0413:39:18\","
				+ "\"TransId\":\"P01C020-2017050407238\",\"VehicleColor\":\"Black\",\"CamName\":\"Lane1\","
				+ "\"Latitude\":\"17.4012488\",\"Longitude\":\"78.4763849\",\"Description\":\"Vehicleisstolen\","
				+ "\"imageUrl\":{\"image\":[\"http://10.10.1.119/TransId.jpg\"]}}]}";

	}

	private String getVehantVehiScanNotficationPayloadCapturedByMultipleCameras() {
		return "{\"Vehicle\":[{\"LicenseNum\":\"AP13X1234\",\"LPCategory\":\"Stolen\",\"Time\":\"2017-05-0413:39:18\","
				+ "\"TransId\":\"P01C020-2017050407238\",\"VehicleColor\":\"Black\",\"CamName\":\"Lane1\",\"Latitude\":\"17.4012488\","
				+ "\"Longitude\":\"78.4763849\",\"Description\":\"Vehicleisstolen\",\"imageUrl\":{\"image\":[\"http://10.10.1.119/TransId.jpg\"]}},"
				+ "{\"LicenseNum\":\"AP13X1234\",\"LPCategory\":\"Stolen\",\"Time\":\"2017-05-0413:49:18\",\"TransId\":\"P01C020-2017050407239\","
				+ "\"VehicleColor\":\"Black\",\"CamName\":\"Lane2\",\"Latitude\":\"17.4013488\",\"Longitude\":\"78.4763849\","
				+ "\"Description\":\"Vehicleisstolen\",\"imageUrl\":{\"image\":[\"http://10.10.1.119/TransId1.jpg\"]}}]}";
	}

}
