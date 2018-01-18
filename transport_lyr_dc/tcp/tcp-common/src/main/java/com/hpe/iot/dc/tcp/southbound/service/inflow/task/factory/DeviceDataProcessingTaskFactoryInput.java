/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory;

import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.tcp.component.model.TCPDCComponentModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class DeviceDataProcessingTaskFactoryInput {

	private final LoggerService loggerService;
	private final ServerSocketToDeviceModel serverSocketToDeviceModel;
	private final ServerClientSocketPool tcpServerClientSocketPool;
	private final TCPDCComponentModel dcComponentModel;

	public DeviceDataProcessingTaskFactoryInput(LoggerService loggerService,
			ServerSocketToDeviceModel serverSocketToDeviceModel, ServerClientSocketPool tcpServerClientSocketPool,
			TCPDCComponentModel dcComponentModel) {
		super();
		this.loggerService = loggerService;
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
		this.tcpServerClientSocketPool = tcpServerClientSocketPool;
		this.dcComponentModel = dcComponentModel;
	}

	public LoggerService getLoggerService() {
		return loggerService;
	}

	public ServerSocketToDeviceModel getServerSocketToDeviceModel() {
		return serverSocketToDeviceModel;
	}

	public ServerClientSocketPool getTcpServerClientSocketPool() {
		return tcpServerClientSocketPool;
	}

	public TCPDCComponentModel getDcComponentModel() {
		return dcComponentModel;
	}

	@Override
	public String toString() {
		return "DeviceDataProcessingTaskFactoryInput [loggerService=" + loggerService + ", serverSocketToDeviceModel="
				+ serverSocketToDeviceModel + ", tcpServerClientSocketPool=" + tcpServerClientSocketPool
				+ ", dcComponentModel=" + dcComponentModel + "]";
	}

}
