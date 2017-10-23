/**
 * 
 */
package com.hpe.iot.model.impl;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.model.impl.json.marshling.DeviceMetaModelAdapter;

/**
 * @author sveera
 *
 */
@XmlJavaTypeAdapter(DeviceMetaModelAdapter.class)
public class JsonPathDeviceMetaModel extends DeviceModelImpl {

	private final String deviceIdJsonPath;
	private final String messageTypeJsonPath;

	public JsonPathDeviceMetaModel(String manufacturer, String modelId, String deviceIdJsonPath, String messageTypeJsonPath) {
		super(manufacturer, modelId);
		this.deviceIdJsonPath = deviceIdJsonPath;
		this.messageTypeJsonPath = messageTypeJsonPath;
	}

	public String getDeviceIdJsonPath() {
		return deviceIdJsonPath;
	}

	public String getMessageTypeJsonPath() {
		return messageTypeJsonPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deviceIdJsonPath == null) ? 0 : deviceIdJsonPath.hashCode());
		result = prime * result + ((messageTypeJsonPath == null) ? 0 : messageTypeJsonPath.hashCode());
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
		JsonPathDeviceMetaModel other = (JsonPathDeviceMetaModel) obj;
		if (deviceIdJsonPath == null) {
			if (other.deviceIdJsonPath != null)
				return false;
		} else if (!deviceIdJsonPath.equals(other.deviceIdJsonPath))
			return false;
		if (messageTypeJsonPath == null) {
			if (other.messageTypeJsonPath != null)
				return false;
		} else if (!messageTypeJsonPath.equals(other.messageTypeJsonPath))
			return false;

		if (this.getManufacturer() == null) {
			if (other.getManufacturer() != null)
				return false;
		} else if (!this.getManufacturer().equals(other.getManufacturer()))
			return false;
		if (this.getModelId() == null) {
			if (other.getModelId() != null)
				return false;
		} else if (!this.getModelId().equals(other.getModelId()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DeviceMetaModel [deviceIdJsonPath=" + deviceIdJsonPath + ", messageTypeJsonPath=" + messageTypeJsonPath
				+ ", getManufacturer()=" + getManufacturer() + ", getModelId()=" + getModelId() + "]";
	}

}
