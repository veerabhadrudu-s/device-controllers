/**
 * 
 */
package com.hpe.iot.mqtt.sensenuts.lighting;

import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS_VERSION;
import static com.hpe.iot.utility.UtilityLogger.logRawDataInHexaDecimalFormat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;
import com.hpe.iot.mqtt.test.base.MqttBaseTestTemplate;
import com.hpe.iot.utility.UtilityLogger;

/**
 * @author sveera
 *
 */
public class SensenutsStreetLightingTest extends MqttBaseTestTemplate {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEVICE_ID = "1234";

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

	@Test
	public void testSensenutsStreetLightingForUplinkNotification() throws InterruptedException {
		logRawDataInHexaDecimalFormat(constructPeriodicMessage(), getClass());
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				constructPeriodicMessage());
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		Assert.assertNotNull("Device info cannot be null", deviceInfo);
		validateDeviceModel(deviceInfo.getDevice());
		logger.debug("Received Device Info is " + deviceInfo);
	}

	@Test
	public void testSensenutsStreetLightingForUplinkPowerOutageAlert() throws InterruptedException {
		logRawDataInHexaDecimalFormat(constructPowerOutageAlertMessage(), getClass());
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				constructPowerOutageAlertMessage());
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		Assert.assertNotNull("Device info cannot be null", deviceInfo);
		validateDeviceModel(deviceInfo.getDevice());
		logger.debug("Received Device Info is " + deviceInfo);
	}

	@Test
	public void testSensenutsStreetLightingForUplinkLuminaireFailureAlert() throws InterruptedException {
		logRawDataInHexaDecimalFormat(constructLuminaireFailureMessage(), getClass());
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				constructLuminaireFailureMessage());
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		Assert.assertNotNull("Device info cannot be null", deviceInfo);
		validateDeviceModel(deviceInfo.getDevice());
		logger.debug("Received Device Info is " + deviceInfo);
	}

	@Test
	public void testSensenutsStreetLightingChangeBrightnessOfLight_DownlinkCommand() throws InterruptedException {
		mockNorthboundDownlinkProducerService
				.publishDownlinkData(createNorthboundOneM2MDownlinkCommandForSensenutsChangeBrightness());
		waitForDCToCompletePayloadProcessing();
		ReceivedMqttMessage downlinkCommand = mqttDevicePayloadHolder.getMqttDeviceData();
		logger.trace("Expected downlinkCommand bytes are "
				+ UtilityLogger.convertArrayOfByteToString(constructChangeLightBrightnessDownlinkCommandMessage()));
		logger.trace("Actual downlinkCommand bytes are "
				+ UtilityLogger.convertArrayOfByteToString(downlinkCommand.getMqttMessage()));
		Assert.assertArrayEquals("Expected and actual downlink commands are not same",
				constructChangeLightBrightnessDownlinkCommandMessage(), downlinkCommand.getMqttMessage());
	}

	private void validateDeviceModel(Device device) {
		assertEquals("Expected and Actual Manufacturer are not same", SENSENUTS, device.getManufacturer());
		assertEquals("Expected and Actual Model are not same", SENSENUTS_MODEL, device.getModelId());
		assertEquals("Expected and Actual DeviceId are not same", DEVICE_ID, device.getDeviceId());
	}

	private byte[] constructPeriodicMessage() {
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

	private byte[] constructPowerOutageAlertMessage() {
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

	private byte[] constructLuminaireFailureMessage() {
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

	private String createNorthboundOneM2MDownlinkCommandForSensenutsChangeBrightness() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?xml-oneM2m oneM2M=\"1.10\"?><ns2:requestPrimitive xmlns:ns2=\"http://www.onem2m.org/xml/protocols\">"
				+ "<operation>5</operation><to>HPE_IoT/Light-1234</to><from>/CSE1000</from><requestIdentifier>45f440d7b169453f8e5f00e15046444b</requestIdentifier><primitiveContent><ns2:notification><notificationEvent><representation xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "{\"m2m:cin\":{\"ty\":4,\"ri\":\"HPE_IoT/Light-1234/downlinkCommand/8969087f43c\",\"pi\":\"HPE_IoT/Light-1234/downlinkCommand\",\"ct\":\"20171009T084338,000232\",\"lt\":\"20171009T084338,000232\",\"rn\":\"8969087f43c\",\"et\":\"20271007T084338,000220\",\"st\":1,\"cr\":\"C070878C6-85f18dc6\",\"cnf\":\"text/plain:0\",\"cs\":352,"
				+ "\"con\":\"{\\\"device\\\":{\\\"manufacturer\\\":\\\"sensenuts\\\",\\\"modelId\\\":\\\"lighting\\\",\\\"version\\\":\\\"1.0\\\",\\\"deviceId\\\":\\\"1234\\\"},\\\"messageType\\\":\\\"downlinkCommand\\\",\\\"payload\\\":{\\\"gatewayId\\\":\\\"1234\\\",\\\"lightId\\\":\\\"0001\\\",\\\"brightness\\\":\\\"50\\\"}}\"}}</representation><operationMonitor>"
				+ "<operation>5</operation><originator>C070878C6-c4c0c7c1</originator></operationMonitor><notificationEventType>1</notificationEventType></notificationEvent><verificationRequest>false</verificationRequest><subscriptionReference>7524845195623549720</subscriptionReference><creator>C070878C6-c4c0c7c1</creator></ns2:notification>"
				+ "</primitiveContent><responseType><responseTypeValue>2</responseTypeValue></responseType></ns2:requestPrimitive>";
	}

	private byte[] constructChangeLightBrightnessDownlinkCommandMessage() {
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
