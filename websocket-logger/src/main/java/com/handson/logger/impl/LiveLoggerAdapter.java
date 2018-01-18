/**
 * 
 */
package com.handson.logger.impl;

import com.handson.logger.LiveLogger;
import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class LiveLoggerAdapter implements LiveLogger {

	private final LoggerService loggerServiceAdaptee;
	private final DeviceModel deviceModel;

	public LiveLoggerAdapter(LoggerService loggerServiceAdaptee, DeviceModel deviceModel) {
		super();
		this.loggerServiceAdaptee = loggerServiceAdaptee;
		this.deviceModel = deviceModel;
	}

	@Override
	public void log(Object message) {
		if (message == null)
			return;
		loggerServiceAdaptee.log(deviceModel, message.toString());
	}

	@Override
	public String toString() {
		return "LiveLoggerAdapter [loggerServiceAdaptee=" + loggerServiceAdaptee + ", deviceModel=" + deviceModel + "]";
	}

}
