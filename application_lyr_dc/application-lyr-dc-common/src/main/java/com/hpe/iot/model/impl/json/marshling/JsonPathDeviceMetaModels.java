/**
 * 
 */
package com.hpe.iot.model.impl.json.marshling;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;

/**
 * @author sveera
 *
 */
@XmlRootElement(name = "devicemodels")
@XmlAccessorType(XmlAccessType.FIELD)
public class JsonPathDeviceMetaModels {

	@XmlElement(name = "devicemodel")
	private List<JsonPathDeviceMetaModel> deviceMetaModels;

	public void setDeviceMetaModels(List<JsonPathDeviceMetaModel> deviceMetaModels) {
		this.deviceMetaModels = deviceMetaModels;
	}

	public List<JsonPathDeviceMetaModel> getDeviceMetaModels() {
		return deviceMetaModels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceMetaModels == null) ? 0 : deviceMetaModels.hashCode());
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
		JsonPathDeviceMetaModels other = (JsonPathDeviceMetaModels) obj;
		if (deviceMetaModels == null) {
			if (other.deviceMetaModels != null)
				return false;
		} else if (!deviceMetaModels.equals(other.deviceMetaModels))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceMetaModels [deviceMetaModels=" + deviceMetaModels + "]";
	}

}
