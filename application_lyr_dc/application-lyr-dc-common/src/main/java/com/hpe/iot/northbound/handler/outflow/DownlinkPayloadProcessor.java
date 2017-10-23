package com.hpe.iot.northbound.handler.outflow;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;

public interface DownlinkPayloadProcessor {

	void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload);

}
