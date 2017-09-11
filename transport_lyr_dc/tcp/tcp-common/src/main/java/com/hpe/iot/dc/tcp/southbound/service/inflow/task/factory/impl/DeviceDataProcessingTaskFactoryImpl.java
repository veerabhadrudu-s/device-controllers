/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.impl;

import com.hpe.iot.dc.tcp.component.model.TCPDCComponentModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.MessageBasedDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.SocketSessionBasedDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.DeviceDataProcessingTaskFactory;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class DeviceDataProcessingTaskFactoryImpl implements DeviceDataProcessingTaskFactory {

	@Override
	public DeviceDataProcessingTask createDeviceDataProcessingTask(ServerSocketToDeviceModel serverSocketToDeviceModel,
			ServerClientSocketPool tcpServerClientSocketPool, TCPDCComponentModel dcComponentModel) {
		return dcComponentModel.getDeviceClientSocketExtractor() != null
				? new SocketSessionBasedDataProcessingTask(10l, dcComponentModel.getSouthBoundDCComponentModel(),
						dcComponentModel.getDeviceClientSocketExtractor(), tcpServerClientSocketPool,
						serverSocketToDeviceModel)
				: new MessageBasedDataProcessingTask(10l, dcComponentModel.getSouthBoundDCComponentModel(),
						tcpServerClientSocketPool, serverSocketToDeviceModel);
	}

}
