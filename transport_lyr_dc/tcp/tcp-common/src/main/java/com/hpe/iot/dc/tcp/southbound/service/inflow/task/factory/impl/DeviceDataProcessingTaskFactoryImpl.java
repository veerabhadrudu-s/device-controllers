/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.impl;

import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.MessageBasedDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.SocketSessionBasedDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.DeviceDataProcessingTaskFactory;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.DeviceDataProcessingTaskFactoryInput;

/**
 * @author sveera
 *
 */
public class DeviceDataProcessingTaskFactoryImpl implements DeviceDataProcessingTaskFactory {

	@Override
	public DeviceDataProcessingTask createDeviceDataProcessingTask(
			DeviceDataProcessingTaskFactoryInput dataProcessingTaskFactoryInput) {
		return dataProcessingTaskFactoryInput.getDcComponentModel().getDeviceClientSocketExtractor() != null
				? new SocketSessionBasedDataProcessingTask(dataProcessingTaskFactoryInput.getLoggerService(), 10l,
						dataProcessingTaskFactoryInput.getDcComponentModel().getSouthBoundDCComponentModel(),
						dataProcessingTaskFactoryInput.getDcComponentModel().getDeviceClientSocketExtractor(),
						dataProcessingTaskFactoryInput.getTcpServerClientSocketPool(),
						dataProcessingTaskFactoryInput.getServerSocketToDeviceModel())
				: new MessageBasedDataProcessingTask(dataProcessingTaskFactoryInput.getLoggerService(), 10l,
						dataProcessingTaskFactoryInput.getDcComponentModel().getSouthBoundDCComponentModel(),
						dataProcessingTaskFactoryInput.getTcpServerClientSocketPool(),
						dataProcessingTaskFactoryInput.getServerSocketToDeviceModel());
	}

}
