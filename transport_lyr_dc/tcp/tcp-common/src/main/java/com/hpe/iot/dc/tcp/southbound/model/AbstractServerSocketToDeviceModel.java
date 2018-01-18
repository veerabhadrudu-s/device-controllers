package com.hpe.iot.dc.tcp.southbound.model;

import static com.hpe.iot.dc.tcp.southbound.model.ProcessingTaskType.MESSAGE_BASED_DATA_PROCESSING;

/**
 * @author sveera
 *
 */
public abstract class AbstractServerSocketToDeviceModel implements ServerSocketToDeviceModel {

	public String getDescription() {
		return "";
	}

	@Override
	public TCPOptions getTCPOptions() {
		return new TCPOptions() {

			@Override
			public ProcessingTaskType getProcessingTaskType() {
				return MESSAGE_BASED_DATA_PROCESSING;
			}

			@Override
			public int getBufferCapacity() {
				return 2048;
			}

			@Override
			public int getSocketBacklogCount() {
				return 10000;
			}
		};
	}

	@Override
	public String toString() {
		return getClass() + "[ Manufacturer=" + getManufacturer() + ", ModelId=" + getModelId() + ", Version="
				+ getVersion() + ", BoundAddress=" + getBoundLocalAddress() + ", PortNumber=" + getPortNumber() + "]";
	}

}
