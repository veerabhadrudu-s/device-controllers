/**
 * 
 */
package com.handson.logger.service;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface LoggerService {

	void log(DeviceModel deviceModel, String message);

}
