package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.OFF;
import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.ON;
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload

import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.mmi.safemate.GPSInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerStatus

import static org.junit.Assert.assertEquals;

/**
 * @author sveera
 *
 */
public class TrackerInfoCreatorTest {

	//@formatter:off
	/*			 			
	 "0c", "02", "01", 			--date
	 "0c", "15", "17", 			--time
	 "24", "c9", "02", "fc", 	--latitude
	 "2a", "ad", "10", "00", 	--longitude
	 "ff", "28", 				--speed
	 "00", "03",					--direction
	 "00",						--flag
	 "40","41", "05", "71",		--TSTATE
	 "64",						--Remaining Battery.
	 "00", "00", "00", "00", "00", "00", "00", -- Reserved for future
	 "fb", "61", "0f","a1"		-- GSM CELL CODE			
	 */
	//@formatter:on

	private static final String[] DEVICE_INFO_TEST_DATA =   [
		"0c",
		"02",
		"01",
		"0c",
		"15",
		"17",
		"24",
		"c9",
		"02",
		"fc",
		"2a",
		"ad",
		"10",
		"00",
		"ff",
		"28",
		"00",
		"03",
		"00",
		"40",
		"41",
		"05",
		"71",
		"64",
		"00",
		"00",
		"00",
		"00",
		"00",
		"00",
		"00",
		"fb",
		"61",
		"0f",
		"a1"] as String[];

	private TrackerInfoCreator trackerInfoCreator;

	@Before
	public void setUp() throws Exception {
		trackerInfoCreator = new TrackerInfoCreator();
	}

	@Test
	public void testConstructTrackerInfo() {
		assertEquals("Expected Tracker Info value and Actual Tracker Info are not equal", getExpectedTrackerInfo(),
				trackerInfoCreator.constructTrackerInfo(getTestData()));
	}

	private byte[] getTestData() {
		return createBinaryPayloadFromHexaPayload(DEVICE_INFO_TEST_DATA, getClass());
	}

	private TrackerInfo getExpectedTrackerInfo() {
		return new TrackerInfo(new GPSInfo("12-2-2001", "12:21:23 GMT", 4228040996/3600000, 1092906/3600000, 377.82, 768), 100,
				new TrackerStatus(ON, OFF, ON, ON, ON, ON, ON, ON, ON, ON));
	}

}
