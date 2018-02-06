package com.hpe.iot.dc.mmi.safemate.testdata;

import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.dc.model.DeviceModelImpl

/**
 * @author sveera
 *
 */
public class MMITestDataCollection {
	
	public static final String MANUFACTURER="MMI";
	public static final String MODEL_ID="SafeMate";
	public static final String VERSION="1.0";	
	public static final DeviceModel SAFEMATE_DEVICE_MODEL=new DeviceModelImpl(MANUFACTURER, MODEL_ID, VERSION);

	//@formatter:off
		/*
		"40", "40", 				--HEAD
		"66", "00",					--Data Length
		"01",						--Protocol Version
		"33","30","31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00", -- Device ID
		"40", "01",					--Protocol Type 			
		"0c", "02", "01", 			--date
		"0c", "15", "17", 			--time
		"24", "c9", "02", "fc", 	--latitude
		"2a", "ad", "10", "00", 	--longitude
		"ff", "28", 				--speed
		"00", "03",					--direction
		"00",						--flag
		"00","00", "00", "00",		--TSTATE
		"64",						--Remaining Battery.
		"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
		"fb", "61", "0f","a1",		-- GSM CELL CODE
		"50", "37", "31", "38", "5f", "53", "20", "43", "45", "20", "49", "4e", "46", "4f", "20", "56", "31","2e", "30", "2e", "31", "00", --Firmware version
		"50", "37", "31", "38", "5f", "48", "20", "56", "31", "2e", "30", "2e", "33", "00", --Hardware version
		"ed", "33",					-- CRC
		"0d", "0a" 					-- TAIL	
		*/
	//@formatter:on	

	public static final String[] IPCONNECT_DATA_MESSAGE_HEX =[ "40", "40", "66", "00", "01", "33", "30",
			"31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00",
			"40", "01", "0c", "02", "01", "0c", "15", "17", "24", "c9", "02", "fc", "2a", "ad", "10", "00", "ff", "28",
			"00", "03", "00", "00", "00", "00", "00", "64", "00", "00", "00", "00", "00", "00", "00", "fb", "61", "0f",
			"a1", "50", "37", "31", "38", "5f", "53", "20", "43", "45", "20", "49", "4e", "46", "4f", "20", "56", "31",
			"2e", "30", "2e", "31", "00", "50", "37", "31", "38", "5f", "48", "20", "56", "31", "2e", "30", "2e", "33",
			"00", "ed", "33", "0d", "0a" ] as String[];

	//@formatter:off
			/*
			"40", "40", 				--HEAD
			"1f", "00",					--Data Length
			"01",						--Protocol Version
			"33","30","31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00", -- Device ID
			"40", "03",					--Protocol Type			
			"d9", "65",					-- CRC
			"0d", "0a" 					-- TAIL	
			*/
	//@formatter:on

	public static final String[] HEART_BEAT_DATA_MESSAGE_HEX = [ "40", "40", "1f", "00", "01", "33", "30",
			"31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00",
			"40", "03", "d9", "65", "0d", "0a" ] as String[];

	//@formatter:off
		/*
		"40", "40", 				--HEAD
		"66", "00",					--Data Length
		"01",						--Protocol Version
		"33","30","31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00", -- Device ID
		"42", "06",					--Protocol Type
		"00"						--flag
		"01"						--pkg_item (No of GPS packets in this Message)
		"04","0c","10", 			--date
		"11","37","13", 			--time
		"98","26","c9","02",	 	--latitude
		"d0","2f","ad","10", 		--longitude
		"0d","00",	 				--speed
		"01","07",					--direction
		"67",						--flag
		"00","00", "00", "00",		--TSTATE
		"64",						--Remaining Battery.
		"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
		"fc","61","74","60",		-- GSM CELL CODE		
		"dc","60",					-- CRC
		"0d","0a" 					-- TAIL	
		*/
	//@formatter:on	

	public static final String[] NOTIFICATION_MESSAGE_HEX = [ "40", "40", "44", "00", "01", "33", "30",
			"31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00",
			"42", "06", "00", "01", "04", "0c", "10", "11", "37", "13", "98", "26", "c9", "02", "d0", "2f", "ad", "10",
			"0d", "00", "01", "07", "67", "00", "00", "00", "00", "64", "00", "00", "00", "00", "00", "00", "00", "fc",
			"61", "74", "60", "dc", "60", "0d", "0a" ] as String[];

	//@formatter:off
			/*
			"40", "40", 				--HEAD
			"42", "00",					--Data Length
			"01",						--Protocol Version
			"33","30","31", "30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00", -- Device ID
			"42", "03",					--Protocol Type
			"06", "0c", "10", 			--date
			"09", "22", "1b",			--time
			"4e", "ee", "c9", "02",	 	--latitude
			"3e", "8a", "ab", "10",		--longitude
			"00", "00",	 				--speed
			"00","00",					--direction
			"03",						--flag
			"40", "00", "00", "00",		--TSTATE
			"64",						--Remaining Battery.
			"00", "00", "00", "00", "00", "00", "00", -- Reserved for future
			"fb", "61", "0f", "a1",		-- GSM CELL CODE		
			"a9", "5c",					-- CRC
			"0d","0a" 					-- TAIL	
			*/
		//@formatter:on	

	public static final String[] ALARM_MESSAGE_HEX =  [ "40", "40", "42", "00", "01", "33", "30", "31",
			"30", "37", "31", "35", "30", "30", "30", "30", "37", "00", "00", "00", "00", "00", "00", "00", "00", "42",
			"03", "06", "0c", "10", "09", "22", "1b", "4e", "ee", "c9", "02", "3e", "8a", "ab", "10", "00", "00", "00",
			"00", "03", "40", "00", "00", "00", "64", "00", "00", "00", "00", "00", "00", "00", "fb", "61", "0f", "a1",
			"a9", "5c", "0d", "0a" ] as String[];

}
