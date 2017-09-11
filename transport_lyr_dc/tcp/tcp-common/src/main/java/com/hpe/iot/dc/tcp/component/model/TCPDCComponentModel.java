/**
 * 
 */
package com.hpe.iot.dc.tcp.component.model;

import com.hpe.iot.dc.northbound.component.model.NorthBoundDCComponentModel;
import com.hpe.iot.dc.southbound.component.model.SouthBoundDCComponentModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;

/**
 * @author sveera
 *
 */
public class TCPDCComponentModel {

	private final NorthBoundDCComponentModel northBoundDCComponentModel;
	private final SouthBoundDCComponentModel southBoundDCComponentModel;
	private DeviceClientSocketExtractor deviceClientSocketExtractor;

	public TCPDCComponentModel(NorthBoundDCComponentModel northBoundDCComponentModel,
			SouthBoundDCComponentModel southBoundDCComponentModel) {
		super();
		this.northBoundDCComponentModel = northBoundDCComponentModel;
		this.southBoundDCComponentModel = southBoundDCComponentModel;
	}

	public TCPDCComponentModel(NorthBoundDCComponentModel northBoundDCComponentModel,
			SouthBoundDCComponentModel southBoundDCComponentModel,
			DeviceClientSocketExtractor deviceClientSocketExtractor) {		
		this(northBoundDCComponentModel, southBoundDCComponentModel);
		this.deviceClientSocketExtractor = deviceClientSocketExtractor;
	}

	public NorthBoundDCComponentModel getNorthBoundDCComponentModel() {
		return northBoundDCComponentModel;
	}

	public SouthBoundDCComponentModel getSouthBoundDCComponentModel() {
		return southBoundDCComponentModel;
	}

	public DeviceClientSocketExtractor getDeviceClientSocketExtractor() {
		return deviceClientSocketExtractor;
	}

	@Override
	public String toString() {
		return "TCPDCComponentModel [northBoundDCComponentModel=" + northBoundDCComponentModel
				+ ", southBoundDCComponentModel=" + southBoundDCComponentModel + ", deviceClientSocketExtractor="
				+ deviceClientSocketExtractor + "]";
	}

}
