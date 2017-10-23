/**
 * 
 */
package com.hpe.iot.dc.model;

/**
 * @author sveera
 *
 */
public class DeviceModelImpl implements DeviceModel {

	private final String manufacturer;
	private final String modelId;

	public DeviceModelImpl(String manufacturer, String modelId) {
		super();
		this.manufacturer = manufacturer;
		this.modelId = modelId;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		DeviceModelImpl other = (DeviceModelImpl) obj;
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
		return "DeviceModelImpl [manufacturer=" + manufacturer + ", modelId=" + modelId + "]";
	}

}
