/**
 * 
 */
package com.handson.logger.constants;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;

/**
 * @author sveera
 *
 */
public interface DeviceModelConstants {

	String VERSION = "1.0";
	String MODEL_ID = "sampleModelId";
	String MANUFACTURER = "sample";
	DeviceModel DEVICE_MODEL = new DeviceModelImpl(MANUFACTURER, MODEL_ID, VERSION);

}
