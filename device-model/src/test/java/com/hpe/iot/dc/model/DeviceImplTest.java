/**
 * 
 */
package com.hpe.iot.dc.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class DeviceImplTest {

	public final DeviceImpl deviceImpl = new DeviceImpl("manufacturer", "modelId", "version", "deviceId");

	@Test
	final public void testDeviceImpl() {
		assertNotNull(deviceImpl);
	}

	@Test
	final public void testGetManufacturer() {
		assertEquals("manufacturer", deviceImpl.getManufacturer());
	}

	@Test
	final public void testGetModelId() {
		assertEquals("modelId", deviceImpl.getModelId());
	}

	@Test
	final public void testGetVersion() {
		assertEquals("version", deviceImpl.getVersion());
	}

	@Test
	final public void testDeviceId() {
		assertEquals("deviceId", deviceImpl.getDeviceId());
	}

	@Test
	final public void testEqualsObject() {
		assertTrue(deviceImpl.equals(deviceImpl));
		assertFalse(deviceImpl.equals(null));
		assertTrue(deviceImpl.equals(new DeviceImpl("manufacturer", "modelId", "version", "deviceId")));
		assertFalse(deviceImpl.equals(new DeviceImpl("manu", "modelId", "version", "deviceId")));
		assertFalse(deviceImpl.equals(new DeviceImpl("manufacturer", "model", "version", "deviceId")));
		assertFalse(deviceImpl.equals(new DeviceImpl("manufacturer", "modelId", "ver", "deviceId")));
		assertFalse(deviceImpl.equals(new DeviceImpl("manufacturer", "modelId", "version", "device")));
		assertFalse(new DeviceImpl(null, "modelId", "version", "deviceId").equals(deviceImpl));
		assertFalse(new DeviceImpl("manufacturer", null, "version", "deviceId").equals(deviceImpl));
		assertFalse(new DeviceImpl("manufacturer", "modelId", null, "deviceId").equals(deviceImpl));
		assertFalse(new DeviceImpl("manufacturer", "modelId", "version", null).equals(deviceImpl));
	}

	@Test
	public void testHashCode() {
		Map<DeviceImpl, DeviceImpl> devices = new HashMap<>();
		devices.put(deviceImpl, deviceImpl);
		assertNotNull(devices.get(new DeviceImpl("manufacturer", "modelId", "version", "deviceId")));
	}

}
