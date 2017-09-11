/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory;

import com.hpe.iot.dc.tcp.component.model.TCPDCComponentModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public interface DeviceDataProcessingTaskFactory {

	DeviceDataProcessingTask createDeviceDataProcessingTask(ServerSocketToDeviceModel serverSocketToDeviceModel,
			ServerClientSocketPool tcpServerClientSocketPool, TCPDCComponentModel dcComponentModel);
}
