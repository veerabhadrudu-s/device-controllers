/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task;

import java.io.IOException;
import java.util.List;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.southbound.component.model.SouthBoundDCComponentModel;
import com.hpe.iot.dc.southbound.service.inflow.activator.SouthBoundServiceActivator;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.DeviceSocketData;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class SocketSessionBasedDataProcessingTask extends DeviceDataProcessingTask {

	private final UplinkDataModelTransformer dataModelTransformer;
	private final SouthBoundServiceActivator southBoundServiceActivator;
	private final ServerClientSocketPool tcpServerClientSocketPool;
	private final ServerSocketToDeviceModel serverSocketToDeviceModel;
	private final DeviceClientSocketExtractor deviceClientSocketExtractor;

	public SocketSessionBasedDataProcessingTask(long pollingPeriod,
			SouthBoundDCComponentModel southBoundDCComponentModel,
			DeviceClientSocketExtractor deviceClientSocketExtractor, ServerClientSocketPool tcpServerClientSocketPool,
			ServerSocketToDeviceModel serverSocketToDeviceModel) {
		super(pollingPeriod);
		this.dataModelTransformer = southBoundDCComponentModel.getDataModelTransformer();
		this.southBoundServiceActivator = southBoundDCComponentModel.getServiceActivator();
		this.tcpServerClientSocketPool = tcpServerClientSocketPool;
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
		this.deviceClientSocketExtractor = deviceClientSocketExtractor;
	}

	@Override
	protected void processSocketData(DeviceSocketData deviceSocketData) throws IOException {
		Device device = deviceClientSocketExtractor.extractConnectedDevice(deviceSocketData.getDataFromClientSocket(),
				deviceSocketData.getClientSocket(), serverSocketToDeviceModel, tcpServerClientSocketPool);
		if (device != null) {
			tcpServerClientSocketPool.addSocketChannel(device, deviceSocketData.getClientSocket());
			List<DeviceInfo> dataFrames = dataModelTransformer.convertToModel(device,
					deviceSocketData.getDataFromClientSocket());

			for (DeviceInfo dataFrame : dataFrames)
				southBoundServiceActivator.processMessage(dataFrame);
		}

	}

}
