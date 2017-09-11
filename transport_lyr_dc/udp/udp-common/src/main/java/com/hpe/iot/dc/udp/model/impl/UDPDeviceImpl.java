/**
 * 
 */
package com.hpe.iot.dc.udp.model.impl;

import java.net.InetAddress;

import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.dc.udp.model.UDPDevice;

/**
 * @author sveera
 *
 */
public class UDPDeviceImpl extends DeviceModelImpl implements UDPDevice {

	private final InetAddress address;
	private final int port;

	public UDPDeviceImpl(String manufacturer, String modelId, InetAddress address, int port) {
		super(manufacturer, modelId);
		this.address = address;
		this.port = port;
	}

	@Override
	public InetAddress getAddress() {
		return address;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UDPDeviceImpl other = (UDPDeviceImpl) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UDPDeviceImpl [address=" + address + ", port=" + port + ", getManufacturer()=" + getManufacturer()
				+ ", getModelId()=" + getModelId() + ", toString()=" + super.toString() + ", getClass()=" + getClass()
				+ "]";
	}

}
