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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class DeviceModelImplTest {

	private final DeviceModelImpl deviceModelImpl = new DeviceModelImpl("manufacturer", "modelId", "version");

	@Test
	final public void testDeviceModelImpl() {
		Assertions.assertNotNull(deviceModelImpl);
	}

	@Test
	final public void testGetManufacturer() {
		assertEquals("manufacturer", deviceModelImpl.getManufacturer());
	}

	@Test
	final public void testGetModelId() {
		assertEquals("modelId", deviceModelImpl.getModelId());
	}

	@Test
	final public void testGetVersion() {
		assertEquals("version", deviceModelImpl.getVersion());
	}

	@Test
	final public void testEqualsObject() {
		assertTrue(deviceModelImpl.equals(deviceModelImpl));
		assertFalse(deviceModelImpl.equals(null));
		assertTrue(deviceModelImpl.equals(new DeviceModelImpl("manufacturer", "modelId", "version")));
		assertFalse(deviceModelImpl.equals(new DeviceModelImpl("manu", "modelId", "version")));
		assertFalse(deviceModelImpl.equals(new DeviceModelImpl("manufacturer", "model", "version")));
		assertFalse(deviceModelImpl.equals(new DeviceModelImpl("manufacturer", "modelId", "ver")));
		assertFalse(new DeviceModelImpl(null, "modelId", "version").equals(deviceModelImpl));
		assertFalse(new DeviceModelImpl("manufacturer", null, "version").equals(deviceModelImpl));
		assertFalse(new DeviceModelImpl("manufacturer", "modelId", null).equals(deviceModelImpl));
	}

	@Test
	public void testHashCode() {
		Map<DeviceModelImpl, DeviceModelImpl> devices = new HashMap<>();
		devices.put(deviceModelImpl, deviceModelImpl);
		assertNotNull(devices.get(new DeviceModelImpl("manufacturer", "modelId", "version")));
	}

}
