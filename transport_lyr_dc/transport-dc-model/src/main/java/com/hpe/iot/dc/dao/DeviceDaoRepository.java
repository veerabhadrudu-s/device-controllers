package com.hpe.iot.dc.dao;

import java.util.Map;

import com.hpe.iot.dc.model.Device;

/**
 * @author sveera
 *
 */
public interface DeviceDaoRepository {
	
	void storeDeviceInformation(Device device,Map<String,String> deviceInformation);
	
	Map<String,String> readDeviceInformation(Device device);

}
