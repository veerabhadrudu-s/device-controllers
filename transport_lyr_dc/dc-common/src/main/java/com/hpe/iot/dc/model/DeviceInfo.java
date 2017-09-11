package com.hpe.iot.dc.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sveera
 *
 */
public class DeviceInfo {

	private final Device device;
	private final String messageType;
	private final Map<String, DeviceData> deviceData = new HashMap<String, DeviceData>();
	private final byte[] rawPayload;

	public DeviceInfo(Device device, String messageType, byte[] rawPayload) {
		super();
		this.device = device;
		this.messageType = messageType;
		this.rawPayload = rawPayload;
	}

	public Device getDevice() {
		return device;
	}

	public String getMessageType() {
		return messageType;
	}

	public byte[] getRawPayload() {
		return rawPayload;
	}

	public Map<String, DeviceData> getDeviceData() {
		return deviceData;
	}

	public void addDeviceData(final String key, final DeviceData value) {
		deviceData.put(key, value);
	}

	@Override
	public String toString() {
		return "DeviceInfo [device=" + device + ", messageType=" + messageType + ", deviceData=" + deviceData
				+ ", rawPayload=" + Arrays.toString(rawPayload) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + ((deviceData == null) ? 0 : deviceData.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + Arrays.hashCode(rawPayload);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceInfo other = (DeviceInfo) obj;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		if (deviceData == null) {
			if (other.deviceData != null)
				return false;
		} else if (!deviceData.equals(other.deviceData))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		if (!Arrays.equals(rawPayload, other.rawPayload))
			return false;
		return true;
	}

}
