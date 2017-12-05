/**
 * 
 */
package com.hpe.iot.mqtt.sensenuts.lighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sveera
 *
 */
public class SensenutsStreetLightingTestData {

	public static final String DEVICE_ID = "1234";

	// Uplink Test Data.
	private final Byte[] notificationByte = new Byte[] { 0x62 };
	private final Byte[] gatewayId = new Byte[] { 0x31, 0x32, 0x33, 0x34 };
	private final Byte[] noOfLights = new Byte[] { 0x02 };
	private final Byte[] light1Id = new Byte[] { 0x30, 0x30, 0x30, 0x31 };
	private final Byte[] light1Voltage = new Byte[] { 0x00, 0x00, 0x00, 0x01 };
	private final Byte[] light1Current = new Byte[] { 0x00, 0x00, 0x00, 0x02 };
	private final Byte[] light1Frequency = new Byte[] { 0x00, 0x00, 0x00, 0x03 };
	private final Byte[] light1PowerFactor = new Byte[] { 0x00, 0x00, 0x00, 0x04 };
	private final Byte[] light1Consumption = new Byte[] { 0x00, 0x00, 0x00, 0x05 };
	private final Byte[] light1BurnTime = new Byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, (byte) 0xE8 };
	private final Byte[] light1DeviceUpTime = new Byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07, (byte) 0xD0 };
	private final Byte[] light1Status = new Byte[] { 0x32 };
	private final Byte[] light2Id = new Byte[] { 0x30, 0x30, 0x30, 0x32 };
	private final Byte[] light2Voltage = new Byte[] { 0x00, 0x00, 0x00, 0x65 };
	private final Byte[] light2Current = new Byte[] { 0x00, 0x00, 0x00, 0x66 };
	private final Byte[] light2Frequency = new Byte[] { 0x00, 0x00, 0x00, 0x67 };
	private final Byte[] light2PowerFactor = new Byte[] { 0x00, 0x00, 0x00, 0x68 };
	private final Byte[] light2Consumption = new Byte[] { 0x00, 0x00, 0x00, 0x69 };
	private final Byte[] light2BurnTime = new Byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x2A, (byte) 0xF8 };
	private final Byte[] light2DeviceUpTime = new Byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x2E, (byte) 0xE0 };
	private final Byte[] light2Status = new Byte[] { 0x64 };

	private final Byte[] alertByte = new Byte[] { 0x63 };
	private final Byte[] lightId = new Byte[] { 0x30, 0x30, 0x30, 0x31 };
	private final Byte[] powerOutageAlertByte = new Byte[] { 0x01 };
	private final Byte[] luminaireFailureAlertByte = new Byte[] { 0x02 };

	// Downlink Test Data.
	private final Byte[] commandByte = new Byte[] { 0x64 };
	private final Byte[] brightnessByte = new Byte[] { 0x32 };

	public byte[] constructPeriodicMessage() {
		List<Byte> periodicMessage = new ArrayList<>();
		periodicMessage.addAll(Arrays.asList(notificationByte));
		periodicMessage.addAll(Arrays.asList(gatewayId));
		periodicMessage.addAll(Arrays.asList(noOfLights));
		periodicMessage.addAll(Arrays.asList(light1Id));
		periodicMessage.addAll(Arrays.asList(light1Voltage));
		periodicMessage.addAll(Arrays.asList(light1Current));
		periodicMessage.addAll(Arrays.asList(light1Frequency));
		periodicMessage.addAll(Arrays.asList(light1PowerFactor));
		periodicMessage.addAll(Arrays.asList(light1Consumption));
		periodicMessage.addAll(Arrays.asList(light1BurnTime));
		periodicMessage.addAll(Arrays.asList(light1DeviceUpTime));
		periodicMessage.addAll(Arrays.asList(light1Status));
		periodicMessage.addAll(Arrays.asList(light2Id));
		periodicMessage.addAll(Arrays.asList(light2Voltage));
		periodicMessage.addAll(Arrays.asList(light2Current));
		periodicMessage.addAll(Arrays.asList(light2Frequency));
		periodicMessage.addAll(Arrays.asList(light2PowerFactor));
		periodicMessage.addAll(Arrays.asList(light2Consumption));
		periodicMessage.addAll(Arrays.asList(light2BurnTime));
		periodicMessage.addAll(Arrays.asList(light2DeviceUpTime));
		periodicMessage.addAll(Arrays.asList(light2Status));
		Byte[] allDataBytes = convertObjectArrayToByteArray(periodicMessage.toArray());
		int checksum = calculateChecksum(allDataBytes);
		periodicMessage.add((byte) checksum);
		return convertObjectArrayToPrimitiveByteArray(periodicMessage.toArray());
	}

	public byte[] constructPowerOutageAlertMessage() {
		List<Byte> powerOutageAlertMessage = new ArrayList<>();
		powerOutageAlertMessage.addAll(Arrays.asList(alertByte));
		powerOutageAlertMessage.addAll(Arrays.asList(gatewayId));
		powerOutageAlertMessage.addAll(Arrays.asList(lightId));
		powerOutageAlertMessage.addAll(Arrays.asList(powerOutageAlertByte));
		Byte[] allDataBytes = convertObjectArrayToByteArray(powerOutageAlertMessage.toArray());
		int checksum = calculateChecksum(allDataBytes);
		powerOutageAlertMessage.add((byte) checksum);
		return convertObjectArrayToPrimitiveByteArray(powerOutageAlertMessage.toArray());
	}

	public byte[] constructLuminaireFailureMessage() {
		List<Byte> luminaireFailureMessage = new ArrayList<>();
		luminaireFailureMessage.addAll(Arrays.asList(alertByte));
		luminaireFailureMessage.addAll(Arrays.asList(gatewayId));
		luminaireFailureMessage.addAll(Arrays.asList(lightId));
		luminaireFailureMessage.addAll(Arrays.asList(luminaireFailureAlertByte));
		Byte[] allDataBytes = convertObjectArrayToByteArray(luminaireFailureMessage.toArray());
		int checksum = calculateChecksum(allDataBytes);
		luminaireFailureMessage.add((byte) checksum);
		return convertObjectArrayToPrimitiveByteArray(luminaireFailureMessage.toArray());
	}

	public byte[] constructChangeLightBrightnessDownlinkCommandMessage() {
		List<Byte> luminaireFailureMessage = new ArrayList<>();
		luminaireFailureMessage.addAll(Arrays.asList(commandByte));
		luminaireFailureMessage.addAll(Arrays.asList(gatewayId));
		luminaireFailureMessage.addAll(Arrays.asList(lightId));
		luminaireFailureMessage.addAll(Arrays.asList(brightnessByte));
		Byte[] allDataBytes = convertObjectArrayToByteArray(luminaireFailureMessage.toArray());
		int checksum = calculateChecksum(allDataBytes);
		luminaireFailureMessage.add((byte) checksum);
		return convertObjectArrayToPrimitiveByteArray(luminaireFailureMessage.toArray());
	}

	private int calculateChecksum(Byte[] allDataBytes) {
		int checksum = 0x00;
		for (Byte dataByte : allDataBytes)
			checksum = checksum ^ dataByte.byteValue();
		return checksum;
	}

	private Byte[] convertObjectArrayToByteArray(Object[] allObject) {
		Byte[] byteData = new Byte[allObject.length];
		for (int index = 0; index < allObject.length; index++)
			byteData[index] = (Byte) allObject[index];
		return byteData;
	}

	private byte[] convertObjectArrayToPrimitiveByteArray(Object[] allObject) {
		byte[] byteData = new byte[allObject.length];
		for (int index = 0; index < allObject.length; index++)
			byteData[index] = (byte) allObject[index];
		return byteData;
	}

}
