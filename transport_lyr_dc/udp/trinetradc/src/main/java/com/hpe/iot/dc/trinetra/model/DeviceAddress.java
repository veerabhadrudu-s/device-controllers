package com.hpe.iot.dc.trinetra.model;

import com.hpe.iot.dc.model.DeviceData;

/**
 * @author sveera
 *
 */
public class DeviceAddress implements DeviceData {

	public static final String DEVICE_ADDRESS = "Device Address";

	private final String deviceIp, port;

	public DeviceAddress(String deviceIp, String port) {
		this.deviceIp = deviceIp;
		this.port = port;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public String getPort() {
		return port;
	}

	@Override
	public String getDeviceDataInformation() {
		return DEVICE_ADDRESS;
	}

	@Override
	public String toString() {
		return "DeviceAddress [deviceIp=" + deviceIp + ", port=" + port + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceIp == null) ? 0 : deviceIp.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceAddress other = (DeviceAddress) obj;
		if (deviceIp == null) {
			if (other.deviceIp != null)
				return false;
		} else if (!deviceIp.equals(other.deviceIp))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		return true;
	}

}
