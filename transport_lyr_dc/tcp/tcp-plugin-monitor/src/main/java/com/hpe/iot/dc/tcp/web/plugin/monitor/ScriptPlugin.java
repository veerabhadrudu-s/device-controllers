/**
 * 
 */
package com.hpe.iot.dc.tcp.web.plugin.monitor;

import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceModelImpl;

public class ScriptPlugin extends DeviceModelImpl {

	private final int connectedDevices;
	private final String description;

	public ScriptPlugin(String manufacturer, String modelId, int connectedDevices, String description) {
		super(manufacturer, modelId);
		this.connectedDevices = connectedDevices;
		this.description = description;
	}

	public int getConnectedDevices() {
		return connectedDevices;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectedDevices;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((getManufacturer() == null) ? 0 : getManufacturer().hashCode());
		result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
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
		ScriptPlugin other = (ScriptPlugin) obj;
		if (connectedDevices != other.connectedDevices)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (getManufacturer() == null) {
			if (other.getManufacturer() != null)
				return false;
		} else if (!getManufacturer().equals(other.getManufacturer()))
			return false;
		if (getModelId() == null) {
			if (other.getModelId() != null)
				return false;
		} else if (!getModelId().equals(other.getModelId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ScriptPlugin [manufacturer=" + getManufacturer() + ", modelId=" + getModelId() + ", connectedDevices="
				+ getConnectedDevices() + ", description=" + getDescription() + "]";
	}

}