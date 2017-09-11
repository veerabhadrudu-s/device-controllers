/**
 * 
 */
package com.hpe.iot.dc.southbound.component.model;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.service.inflow.activator.SouthBoundServiceActivator;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class SouthBoundDCComponentModel {

	private final UplinkDataModelTransformer dataModelTransformer;
	private final SouthBoundServiceActivator serviceActivator;
	private final DeviceModel deviceModel;

	public SouthBoundDCComponentModel(UplinkDataModelTransformer dataModelTransformer,
			SouthBoundServiceActivator serviceActivator, DeviceModel deviceModel) {
		super();
		this.dataModelTransformer = dataModelTransformer;
		this.serviceActivator = serviceActivator;
		this.deviceModel = deviceModel;
	}

	public UplinkDataModelTransformer getDataModelTransformer() {
		return dataModelTransformer;
	}

	public SouthBoundServiceActivator getServiceActivator() {
		return serviceActivator;
	}

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	@Override
	public String toString() {
		return "SouthBoundDCComponentModel [dataModelTransformer=" + dataModelTransformer + ", serviceActivator="
				+ serviceActivator + ", deviceModel=" + deviceModel + "]";
	}

}
