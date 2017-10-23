/**
 * 
 */
package com.hpe.iot.model.impl.json.marshling;

/**
 * @author sveera
 *
 */
public class AdaptedDeviceMetaModel {

	private String manufacturer;
	private String modelId;
	private String version;
	private String deviceIdJsonPath;
	private String messageTypeJsonPath;

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDeviceIdJsonPath() {
		return deviceIdJsonPath;
	}

	public void setDeviceIdJsonPath(String deviceIdJsonPath) {
		this.deviceIdJsonPath = deviceIdJsonPath;
	}

	public String getMessageTypeJsonPath() {
		return messageTypeJsonPath;
	}

	public void setMessageTypeJsonPath(String messageTypeJsonPath) {
		this.messageTypeJsonPath = messageTypeJsonPath;
	}

}
