package com.hpe.iot.dc.northbound.component.meta.model;

import java.util.ArrayList;
import java.util.List;

import com.hpe.iot.dc.northbound.converter.inflow.IOTModelConverter;
import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class NorthBoundDCComponentMetaModel {

	private Class<? extends IOTModelConverter> iotModelConverterClass;
	private Class<? extends DownlinkDataModelTransformer> dataModelTransformerType;
	private final List<Class<? extends DownlinkDeviceDataConverter>> deviceDataConverterTypes;

	public NorthBoundDCComponentMetaModel() {
		super();
		this.deviceDataConverterTypes = new ArrayList<>();
	}

	public Class<? extends IOTModelConverter> getIotModelConverterClass() {
		return iotModelConverterClass;
	}

	public void setIotModelConverterClass(Class<? extends IOTModelConverter> iotModelConverterClass) {
		this.iotModelConverterClass = iotModelConverterClass;
	}

	public void addDeviceDataConverterType(Class<? extends DownlinkDeviceDataConverter> deviceDataConverterType) {
		deviceDataConverterTypes.add(deviceDataConverterType);
	}

	public List<Class<? extends DownlinkDeviceDataConverter>> getDeviceDataConverterTypes() {
		return deviceDataConverterTypes;
	}

	public Class<? extends DownlinkDataModelTransformer> getDataModelTransformerType() {
		return dataModelTransformerType;
	}

	public void setDataModelTransformerType(Class<? extends DownlinkDataModelTransformer> dataModelTransformerType) {
		this.dataModelTransformerType = dataModelTransformerType;
	}

	@Override
	public String toString() {
		return "NorthBoundDCComponentModel [iotModelConverterClass=" + iotModelConverterClass
				+ ", dataModelTransformerType=" + dataModelTransformerType + ", deviceDataConverterTypes="
				+ deviceDataConverterTypes + "]";
	}

}
