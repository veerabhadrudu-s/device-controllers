/**
 * 
 */
package com.hpe.iot.dc.northbound.component.model;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.northbound.service.outflow.activator.NorthBoundServiceActivator;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class NorthBoundDCComponentModel {

	private final DownlinkDataModelTransformer dataModelTransformer;
	private final NorthBoundServiceActivator serviceActivator;
	private final DeviceModel deviceModel;

	public NorthBoundDCComponentModel(DownlinkDataModelTransformer dataModelTransformer,
			NorthBoundServiceActivator serviceActivator, DeviceModel deviceModel) {
		super();
		this.dataModelTransformer = dataModelTransformer;
		this.serviceActivator = serviceActivator;
		this.deviceModel = deviceModel;
	}

	public DownlinkDataModelTransformer getDataModelTransformer() {
		return dataModelTransformer;
	}

	public NorthBoundServiceActivator getServiceActivator() {
		return serviceActivator;
	}

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	@Override
	public String toString() {
		return "NorthBoundDCComponentModel [dataModelTransformer=" + dataModelTransformer + ", serviceActivator="
				+ serviceActivator + ", deviceModel=" + deviceModel + "]";
	}

}
