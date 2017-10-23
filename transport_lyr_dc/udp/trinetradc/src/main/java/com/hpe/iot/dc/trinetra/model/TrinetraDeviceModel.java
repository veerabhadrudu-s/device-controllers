/**
 * 
 */
package com.hpe.iot.dc.trinetra.model;

import org.springframework.stereotype.Component;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
@Component
public class TrinetraDeviceModel implements DeviceModel {

	private static final String VEHICAL_TRACKING = "VehicalTracking";
	private static final String TRINETRA = "Trinetra";

	@Override
	public String getManufacturer() {
		return TRINETRA;
	}

	@Override
	public String getModelId() {
		return VEHICAL_TRACKING;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
