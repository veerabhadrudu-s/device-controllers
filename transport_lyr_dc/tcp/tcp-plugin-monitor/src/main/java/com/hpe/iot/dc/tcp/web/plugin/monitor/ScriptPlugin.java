/**
 * 
 */
package com.hpe.iot.dc.tcp.web.plugin.monitor;

public class ScriptPlugin {

	private final String manufacturer;
	private final String modelId;
	private final int connectedDevices;
	private final String description;

	public ScriptPlugin(String manufacturer, String modelId, int connectedDevices, String description) {
		super();
		this.manufacturer = manufacturer;
		this.modelId = modelId;
		this.connectedDevices = connectedDevices;
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModelId() {
		return modelId;
	}

	public int getConnectedDevices() {
		return connectedDevices;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "ScriptPlugin [manufacturer=" + getManufacturer() + ", modelId=" + getModelId() + ", connectedDevices="
				+ getConnectedDevices() + ", description=" + getDescription() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectedDevices;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		ScriptPlugin other = (ScriptPlugin) obj;
		if (connectedDevices != other.connectedDevices)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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

}