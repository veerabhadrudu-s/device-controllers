/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task;

import java.io.IOException;
import java.util.List;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.southbound.component.model.SouthBoundDCComponentModel;
import com.hpe.iot.dc.southbound.service.inflow.activator.SouthBoundServiceActivator;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.DeviceSocketData;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class MessageBasedDataProcessingTask extends DeviceDataProcessingTask {

	private final UplinkDataModelTransformer dataModelTransformer;
	private final SouthBoundServiceActivator southBoundServiceActivator;
	private final ServerClientSocketPool tcpServerClientSocketPool;
	private final ServerSocketToDeviceModel serverSocketToDeviceModel;

	public MessageBasedDataProcessingTask(long pollingPeriod, SouthBoundDCComponentModel southBoundDCComponentModel,
			ServerClientSocketPool tcpServerClientSocketPool, ServerSocketToDeviceModel serverSocketToDeviceModel) {
		super(pollingPeriod);
		this.dataModelTransformer = southBoundDCComponentModel.getDataModelTransformer();
		this.southBoundServiceActivator = southBoundDCComponentModel.getServiceActivator();
		this.tcpServerClientSocketPool = tcpServerClientSocketPool;
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
	}

	@Override
	protected void processSocketData(DeviceSocketData deviceSocketData) throws IOException {
		List<DeviceInfo> dataFrames = dataModelTransformer.convertToModel(serverSocketToDeviceModel,
				deviceSocketData.getDataFromClientSocket());
		if (dataFrames != null && dataFrames.size() > 0) {
			tcpServerClientSocketPool.addSocketChannel(dataFrames.get(0).getDevice(),
					deviceSocketData.getClientSocket());
			for (DeviceInfo dataFrame : dataFrames)
				southBoundServiceActivator.processMessage(dataFrame);
		}

	}

}
