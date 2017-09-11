/**
 * 
 */
package com.hpe.iot.model.factory;

import java.util.List;

import com.hpe.iot.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface DeviceModelFactory {

	DeviceModel findDeviceModel(String manufacturer, String modelId);

	List<DeviceModel> getAllDeviceModels();

}
