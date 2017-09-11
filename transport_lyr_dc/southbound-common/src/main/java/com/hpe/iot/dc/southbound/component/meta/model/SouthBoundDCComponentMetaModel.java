package com.hpe.iot.dc.southbound.component.meta.model;

import java.util.ArrayList;
import java.util.List;

import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class SouthBoundDCComponentMetaModel {

	private List<Class<? extends UplinkDeviceDataConverter>> deviceDataConverterTypes;
	private Class<? extends UplinkDataModelTransformer> dataModelTransformerType;

	public SouthBoundDCComponentMetaModel() {
		super();
		this.deviceDataConverterTypes = new ArrayList<>();
	}

	public void addDeviceDataConverterType(Class<? extends UplinkDeviceDataConverter> deviceDataConverterType) {
		deviceDataConverterTypes.add(deviceDataConverterType);
	}

	public List<Class<? extends UplinkDeviceDataConverter>> getDeviceDataConverterTypes() {
		return deviceDataConverterTypes;
	}

	public Class<? extends UplinkDataModelTransformer> getDataModelTransformerType() {
		return dataModelTransformerType;
	}

	public void setDataModelTransformerType(Class<? extends UplinkDataModelTransformer> dataModelTransformerType) {
		this.dataModelTransformerType = dataModelTransformerType;
	}

	@Override
	public String toString() {
		return "SouthBoundDCComponentModel [deviceDataConverterTypes=" + deviceDataConverterTypes
				+ ", dataModelTransformerType=" + dataModelTransformerType + "]";
	}

}
