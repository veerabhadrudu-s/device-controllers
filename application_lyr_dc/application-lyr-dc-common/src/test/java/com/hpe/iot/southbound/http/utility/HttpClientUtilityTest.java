/**
 * 
 */
package com.hpe.iot.southbound.http.utility;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sveera
 *
 */
public class HttpClientUtilityTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final HttpClientUtility httpClientUtility = new HttpClientUtility();

	@Test
	@Ignore
	public void testGetWeatherServiceApiOverHttps() throws URISyntaxException, ClientProtocolException, IOException,
			KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
		URI uri = new URIBuilder().setScheme("https").setHost("api.sunrise-sunset.org").setPort(443).setPath("/json")
				.setParameter("lat", "36.7201600").setParameter("lng", "-4.4203400").build();
		logger.trace(uri.toString());
		String data = httpClientUtility.getResourceOnHttps(uri.toString(), "src/test/resources/cert/weather-der.cer");
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Ignore
	public void testGetPartnerUiotDashboardPageOverHttps() throws URISyntaxException, ClientProtocolException,
			IOException, KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
		String data = httpClientUtility.getResourceOnHttps(
				"https://185.170.48.160:13080/auth/realms/saml-demo/protocol/saml",
				"src/test/resources/cert/partner-uiot-der.cer", false);
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Ignore
	public void testGetAssetByNameOverHttp() throws ClientProtocolException, IOException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Basic " + new String(Base64.getEncoder().encode("motwane:Motwane~1".getBytes())));
		String data = httpClientUtility
				.getResourceOnHttp("http://10.3.239.70/dsm/wsi/asset/getAssetByName/Motwane-S000003", headers);
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Deprecated
	@Ignore
	public void testGetAssetByNameOverHttpUsingBasicAuth() throws URISyntaxException, ClientProtocolException,
			IOException, KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
		String data = httpClientUtility.getResourceOnHttpUsingBasicAuth(
				"http://10.3.239.70/dsm/wsi/asset/getAssetByName/MMIS-17013P1R8ATEST07", "mmiindia", "Mmiindia~1");
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Ignore
	public void testPostRequestOnHttpForSafemateAssetTracker() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("Host", "10.3.239.75:80");
		headers.put("Content-Type", "application/vnd.onem2m-ntfy+json");
		headers.put("X-M2M-Origin", "/CSE1000");
		headers.put("X-M2M-RI", "3af1e06da6b44c62ac6acd1fdc9e9636");
		String httpBody = oneM2MNotificationPayloadForSafemateAssetTracker();
		String data = httpClientUtility.postRequestOnHttp(
				"http://115.112.105.107:4530/HPUIOTIntegrationService/ConsumeSOSPayload?rt=2", headers, httpBody);
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Ignore
	public void testPostRequestOnHttpsDrivemateOBD2Sensor() throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("Host", "10.3.239.75:80");
		headers.put("Content-Type", "application/vnd.onem2m-ntfy+json");
		headers.put("X-M2M-Origin", "/CSE1000");
		headers.put("X-M2M-RI", "3af1e06da6b44c62ac6acd1fdc9e9636");
		String httpBody = oneM2MNotificationPayloadForDrivemateOBD2Sensor();
		String data = httpClientUtility.postRequestOnHttps(
				"https://commandcenter.nectechnologies.in:8443/scc/vehicleTrackingInfo?rt=2", headers, httpBody,
				"src/test/resources/cert/NEC.cer");
		Assert.assertNotNull("Response cannot be null", data);
	}

	@Test
	@Ignore
	public void testPostRequestOnHttpDrivemateOBD2Sensor() throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("Host", "10.3.239.75:80");
		headers.put("Content-Type", "application/vnd.onem2m-ntfy+json");
		headers.put("X-M2M-Origin", "/CSE1000");
		headers.put("X-M2M-RI", "3af1e06da6b44c62ac6acd1fdc9e9636");
		String httpBody = oneM2MNotificationPayloadForDrivemateOBD2Sensor();
		String data = httpClientUtility.postRequestOnHttp(
				"https://commandcenter.nectechnologies.in:8443/scc/vehicleTrackingInfo?rt=2", headers, httpBody);
		Assert.assertNotNull("Response cannot be null", data);
	}

	private String oneM2MNotificationPayloadForSafemateAssetTracker() {
		return "{\"m2m:sgn\":{\"nev\":{\"rep\":\"{\\\"m2m:cin\\\":{\\\"ty\\\":4,\\\"ri\\\":\\\"HPE_IoT/MMI-301071500007/ipconnect/95400c91185\\\","
				+ "\\\"pi\\\":\\\"HPE_IoT/MMI-301071500007/ipconnect\\\",\\\"ct\\\":\\\"20170811T093149,000842\\\",\\\"lt\\\":\\\"20170811T093149,000842\\\","
				+ "\\\"rn\\\":\\\"95400c91185\\\",\\\"et\\\":\\\"20270809T093149,000832\\\",\\\"st\\\":1,\\\"cr\\\":\\\"Ccc01090a-02f6c78d\\\","
				+ "\\\"cnf\\\":\\\"text/plain:0\\\",\\\"cs\\\":1736,"
				+ "\\\"con\\\":\\\"{\\\\\\\"device\\\\\\\":{\\\\\\\"manufacturer\\\\\\\":\\\\\\\"MMI\\\\\\\",\\\\\\\"modelId\\\\\\\":\\\\\\\"SafeMate\\\\\\\","
				+ "\\\\\\\"deviceId\\\\\\\":\\\\\\\"301071500007\\\\\\\"},\\\\\\\"messageType\\\\\\\":\\\\\\\"0x4001\\\\\\\",\\\\\\\"deviceData\\\\\\\":{\\\\\\\"Tracker Notification\\\\\\\":"
				+ "{\\\\\\\"notificationType\\\\\\\":\\\\\\\"IPCONNECT\\\\\\\",\\\\\\\"packageItem\\\\\\\":1,"
				+ "\\\\\\\"packageData\\\\\\\":[{\\\\\\\"gpsInformation\\\\\\\":{\\\\\\\"date\\\\\\\":\\\\\\\"11-8-2017\\\\\\\",\\\\\\\"time\\\\\\\":\\\\\\\"9:31:48 GMT\\\\\\\","
				+ "\\\\\\\"latitude\\\\\\\":12.98248,\\\\\\\"longitude\\\\\\\":77.7179633333,\\\\\\\"speed\\\\\\\":0.0,\\\\\\\"direction\\\\\\\":0},\\\\\\\"batteryPercentage\\\\\\\":100,"
				+ "\\\\\\\"trackerStatus\\\\\\\":{\\\\\\\"emergency\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"accelaration\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"speeding\\\\\\\":\\\\\\\"OFF\\\\\\\","
				+ "\\\\\\\"gpsFailure\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"lowPower\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"powerFailure\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"crashAlarm\\\\\\\":\\\\\\\"OFF\\\\\\\","
				+ "\\\\\\\"geoFenceBreach\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"towAlarm\\\\\\\":\\\\\\\"OFF\\\\\\\",\\\\\\\"powerCut\\\\\\\":\\\\\\\"OFF\\\\\\\"}}]}},"
				+ "\\\\\\\"rawPayload\\\\\\\":[64,64,102,0,1,51,48,49,48,55,49,53,48,48,48,48,55,0,0,0,0,0,0,0,0,64,1,11,8,17,9,31,48,32,38,-55,2,-36,44,-83,16,0,0,0,0,3,0,0,0,0,100,0,0,0,0,0,0,0,-5,97,15,-95,80,55,49,56,95,83,32,67,69,32,73,78,70,79,32,86,49,46,48,46,49,0,80,55,49,56,95,72,32,86,49,46,48,46,51,0,-26,-127,13,10]}\\\"}}"
				+ "\",\"om\":{\"op\":5,\"org\":\"Ccc01090a-f933196b\"},\"net\":1},\"vrq\":false,\"sur\":\"7247451247775403870\",\"cr\":\"Ccc01090a-f933196b\"}}";
	}

	private String oneM2MNotificationPayloadForDrivemateOBD2Sensor() {
		return "{\"m2m:sgn\":{\"nev\":{\"rep\":\"{\\\"m2m:cin\\\":{\\\"ty\\\":4,\\\"ri\\\":\\\"HPE_IoT/MMID-356173069019185/notification/3112b13ca9a\\\","
				+ "\\\"pi\\\":\\\"HPE_IoT/MMID-356173069019185/notification\\\",\\\"ct\\\":\\\"20170831T093635,000350\\\",\\\"lt\\\":\\\"20170831T093635,000350\\\","
				+ "\\\"rn\\\":\\\"3112b13ca9a\\\",\\\"et\\\":\\\"20270829T093635,000347\\\",\\\"st\\\":1,\\\"cr\\\":\\\"Ccc01090a-7ed4dbcd\\\",\\\"cnf\\\":\\\"text/plain:0\\\","
				+ "\\\"cs\\\":1428,\\\"con\\\":\\\"{\\\\\\\"device\\\\\\\":{\\\\\\\"manufacturer\\\\\\\":\\\\\\\"MMI\\\\\\\",\\\\\\\"modelId\\\\\\\":\\\\\\\"Drivemate\\\\\\\","
				+ "\\\\\\\"deviceId\\\\\\\":\\\\\\\"356173069019185\\\\\\\"},\\\\\\\"messageType\\\\\\\":\\\\\\\"notification\\\\\\\","
				+ "\\\\\\\"deviceData\\\\\\\":{\\\\\\\"notification\\\\\\\":{\\\\\\\"noOfRecords\\\\\\\":1,\\\\\\\"notificationRecords\\\\\\\":[{\\\\\\\"GPSDateTime\\\\\\\":\\\\\\\"1504172183\\\\\\\",\\\\\\\"Latitude\\\\\\\":\\\\\\\"12.9975546\\\\\\\",\\\\\\\"Longitude\\\\\\\":\\\\\\\"77.6890218\\\\\\\",\\\\\\\"Heading\\\\\\\":\\\\\\\"203\\\\\\\",\\\\\\\"GPS_VSSSpeed\\\\\\\":0,\\\\\\\"CustomInfo\\\\\\\":{\\\\\\\"GPSSatelliteUsed\\\\\\\":17,\\\\\\\"GPSAltitude\\\\\\\":\\\\\\\"907\\\\\\\",\\\\\\\"Ignition\\\\\\\":\\\\\\\"0\\\\\\\",\\\\\\\"GSMSignalQuality\\\\\\\":\\\\\\\"5\\\\\\\",\\\\\\\"MainPowerVoltage\\\\\\\":\\\\\\\"12819\\\\\\\",\\\\\\\"GPS speed (km/h)\\\\\\\":\\\\\\\"0\\\\\\\",\\\\\\\"BackupBatteryVoltage\\\\\\\":\\\\\\\"4032\\\\\\\",\\\\\\\"Battery current (mA)\\\\\\\":\\\\\\\"0\\\\\\\"}}]}},\\\\\\\"rawPayload\\\\\\\":[0,0,0,0,0,0,0,62,8,1,0,0,1,94,55,-90,13,-40,0,46,78,103,106,7,-65,68,-6,3,-117,0,-53,17,0,0,0,10,3,1,0,-16,0,21,5,6,-74,0,6,66,50,19,24,0,0,-51,67,111,67,15,-64,68,0,0,1,-15,0,0,-99,-3,0,1,0,0,114,-62]}\\\"}}\",\"om\":{\"op\":5,\"org\":\"Ccc01090a-f933196b\"},\"net\":1},\"vrq\":false,\"sur\":\"4946468072979676928\",\"cr\":\"Ccc01090a-f933196b\"}}";
	}

}
