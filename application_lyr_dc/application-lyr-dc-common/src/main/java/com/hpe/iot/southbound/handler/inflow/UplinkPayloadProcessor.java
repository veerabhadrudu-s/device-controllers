package com.hpe.iot.southbound.handler.inflow;

import com.hpe.iot.model.DeviceInfo;

public interface UplinkPayloadProcessor {

	void processPayload(DeviceInfo decipheredPayload);
	
}
