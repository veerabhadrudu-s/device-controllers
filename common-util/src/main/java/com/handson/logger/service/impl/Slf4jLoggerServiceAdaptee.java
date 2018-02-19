/**
 * 
 */
package com.handson.logger.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class Slf4jLoggerServiceAdaptee implements LoggerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void log(DeviceModel deviceModel, String message) {
		logger.trace(message);
	}

}
