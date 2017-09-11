package com.hpe.iot.dc.trinetra.dao.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.hpe.iot.dc.dao.DeviceDaoRepository;
import com.hpe.iot.dc.model.Device;

/**
 * @author sveera
 *
 */
@Component
public class InMemoryDeviceDaoRepository implements DeviceDaoRepository {

	private Map<Device, Map<String, String>> deviceRepository = new ConcurrentHashMap<>();

	@Override
	public void storeDeviceInformation(Device device, Map<String, String> deviceInformation) {
		deviceRepository.put(device, deviceInformation);
	}

	@Override
	public Map<String, String> readDeviceInformation(Device device) {
		return deviceRepository.get(device);
	}

}
