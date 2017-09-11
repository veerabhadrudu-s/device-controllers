/**
 * 
 */
package com.hpe.iot.dc.northbound.service.manager;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.northbound.component.manager.outflow.NorthBoundDownlinkComponentManager;
import com.hpe.iot.dc.northbound.service.manager.NorthBoundDownlinkServiceManager;

/**
 * @author sveera
 *
 */
public class NorthBoundDownlinkServiceManagerTest {

	private NorthBoundDownlinkComponentManager northBoundDownlinkComponentManager;
	private NorthBoundDownlinkServiceManager northBoundDownlinkServiceManager;
	private static final String SAMPLE_DOWNLINK_DATA = "{\"device\":{\"manufacturer\":\"MMI\",\"modelId\":\"SafeMate\",\"deviceId\":\"301071500890\"},"
			+ "\"messageType\":\"0x4206\","
			+ "\"deviceData\":{\"Tracker Notification\":{\"notificationType\":\"REGULAR_DATA\",\"packageItem\":1,\"packageData\":[{\"gpsInformation\":{\"date\":\"23-2-2017\",\"time\":\"8:1:36 GMT\",\"latitude\":46787178,\"longitude\":279678318,\"speed\":0,\"direction\":0},\"batteryPercentage\":100,\"trackerStatus\":{\"emergency\":\"OFF\",\"accelaration\":\"OFF\",\"speeding\":\"OFF\",\"gpsFailure\":\"OFF\",\"lowPower\":\"OFF\",\"powerFailure\":\"OFF\",\"crashAlarm\":\"OFF\",\"geoFenceBreach\":\"OFF\",\"towAlarm\":\"OFF\",\"powerCut\":\"OFF\"}}]}},"
			+ "\"rawPayload\":[64,64,68,0,1,51,48,49,48,55,49,53,48,48,56,57,48,0,0,0,0,0,0,0,0,66,6,0,1,23,2,17,8,1,36,106,-22,-55,2,110,-115,-85,16,0,0,0,0,3,0,0,0,0,100,0,0,0,0,0,0,0,-5,97,15,-95,120,85,13,10]}";

	@Before
	public void setUp() throws Exception {
		northBoundDownlinkComponentManager = new NorthBoundDownlinkComponentManager();
		northBoundDownlinkServiceManager = new NorthBoundDownlinkServiceManager(northBoundDownlinkComponentManager);

	}

	@Test
	public void testParsingWithSamplePayload() {
		JsonParser parser = new JsonParser();
		JsonObject jsonRequest = parser.parse(SAMPLE_DOWNLINK_DATA).getAsJsonObject();
		northBoundDownlinkServiceManager.sendDownlinkData(jsonRequest);
	}

}
