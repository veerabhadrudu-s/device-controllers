/**
 * 
 */
package com.hpe.iot.dc.model;

/**
 * @author sveera
 *
 */
public class DeviceImpl implements Device {

	private final String manufacturer;
	private final String modelId;
	private final String deviceId;
	private final String version;

	public DeviceImpl(String manufacturer, String modelId, String version, String deviceId) {
		super();
		this.manufacturer = manufacturer;
		this.modelId = modelId;
		this.deviceId = deviceId;
		this.version = version;
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

	public String getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceImpl [manufacturer=" + manufacturer + ", modelId=" + modelId + ", deviceId=" + deviceId
				+ ", version=" + version + "]";
	}

}
