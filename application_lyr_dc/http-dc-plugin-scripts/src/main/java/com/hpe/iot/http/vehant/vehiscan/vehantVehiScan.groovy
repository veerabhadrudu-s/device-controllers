/**
 * 
 */
package com.hpe.iot.http.vehant.vehiscan

import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.southbound.handler.inflow.impl.AbstractJsonPathDeviceIdExtractor

/**
 * @author sveera
 *
 */
public class VehantVehiScanModel implements DeviceModel {

	@Override
	public String getManufacturer() {
		return "vehant";
	}

	@Override
	public String getModelId() {
		return 'vehiscan';
	}

	@Override
	public String getVersion() {
		return '1.0';
	}
}

public class VehantVehiScanDeviceIdExtractor extends AbstractJsonPathDeviceIdExtractor{

	@Override
	public String getDeviceIdJsonPath() {
		return '$.Vehicle.CamName';
	}
}
