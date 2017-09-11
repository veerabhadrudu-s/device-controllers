/**
 * 
 */
package com.hpe.iot.model;

/**
 * @author sveera
 *
 */
public class DeviceImpl implements Device {

	private final String manufacturer;
	private final String modelId;
	private final String deviceId;

	public DeviceImpl(String manufacturer, String modelId, String deviceId) {
		super();
		this.manufacturer = manufacturer;
		this.modelId = modelId;
		this.deviceId = deviceId;
	}

	@Override
	public String getManufacturer() {
		return manufacturer;
	}

	@Override
	public String getModelId() {
		return modelId;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
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
		DeviceImpl other = (DeviceImpl) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (manufacturer == null) {
			if (other.manufacturer != null)
				return false;
		} else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (modelId == null) {
			if (other.modelId != null)
				return false;
		} else if (!modelId.equals(other.modelId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceImpl [manufacturer=" + manufacturer + ", modelId=" + modelId + ", deviceId=" + deviceId + "]";
	}

}
