/**
 * 
 */
package com.hpe.iot.model.impl.json.marshling;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;

/**
 * @author sveera
 *
 */
public class DeviceMetaModelAdapter extends XmlAdapter<AdaptedDeviceMetaModel, JsonPathDeviceMetaModel> {

	@Override
	public JsonPathDeviceMetaModel unmarshal(AdaptedDeviceMetaModel adaptedDeviceMetaModel) throws Exception {
		return new JsonPathDeviceMetaModel(adaptedDeviceMetaModel.getManufacturer(), adaptedDeviceMetaModel.getModelId(),
				adaptedDeviceMetaModel.getDeviceIdJsonPath(), adaptedDeviceMetaModel.getMessageTypeJsonPath());
	}

	@Override
	public AdaptedDeviceMetaModel marshal(JsonPathDeviceMetaModel deviceMetaModel) throws Exception {
		AdaptedDeviceMetaModel adaptedDeviceMetaModel = new AdaptedDeviceMetaModel();
		adaptedDeviceMetaModel.setManufacturer(deviceMetaModel.getManufacturer());
		adaptedDeviceMetaModel.setModelId(deviceMetaModel.getModelId());
		adaptedDeviceMetaModel.setDeviceIdJsonPath(deviceMetaModel.getDeviceIdJsonPath());
		adaptedDeviceMetaModel.setMessageTypeJsonPath(deviceMetaModel.getMessageTypeJsonPath());
		return adaptedDeviceMetaModel;
	}

}
