/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory;

import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;

/**
 * @author sveera
 *
 */
public interface DeviceDataProcessingTaskFactory {

	DeviceDataProcessingTask createDeviceDataProcessingTask(
			DeviceDataProcessingTaskFactoryInput dataProcessingTaskFactoryInput);
}
