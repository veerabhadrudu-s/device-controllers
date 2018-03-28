
package com.hpe.iot.dc.tcp.southbound.model;

import com.hpe.iot.dc.tcp.southbound.model.AbstractServerSocketToDeviceModel;

/**
 * @author sveera
 *
 */
public class ServerSocketToDeviceModelDummy extends AbstractServerSocketToDeviceModel {

	@Override
	public String getBoundLocalAddress() {
		return null;
	}

	@Override
	public int getPortNumber() {
		return 0;
	}

	@Override
	public String getManufacturer() {
		return null;
	}

	@Override
	public String getModelId() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

}
