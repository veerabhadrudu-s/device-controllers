/**
 * 
 */
package com.handson.logger.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.DeploymentLoggerService;

/**
 * @author sveera
 *
 */
public class Slf4jDeploymentLoggerService implements DeploymentLoggerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void log(String pluginScriptFile, String message) {
		logger.trace(message);
	}

}
