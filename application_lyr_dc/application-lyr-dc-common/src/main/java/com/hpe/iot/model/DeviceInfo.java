package com.hpe.iot.model;

import com.google.gson.JsonObject;

/**
 * @author sveera
 *
 */
public class DeviceInfo {

	private final Device device;
	private final String messageType;
	private final JsonObject payload;

	public DeviceInfo(Device device, String messageType, JsonObject payload) {
		super();
		this.device = device;
		this.messageType = messageType;
		this.payload = payload;
	}

	public Device getDevice() {
		return device;
	}

	public String getMessageType() {
		return messageType;
	}

	public JsonObject getPayload() {
		return payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
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
		DeviceInfo other = (DeviceInfo) obj;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceInfo [device=" + device + ", messageType=" + messageType + ", payload=" + payload + "]";
	}

}
