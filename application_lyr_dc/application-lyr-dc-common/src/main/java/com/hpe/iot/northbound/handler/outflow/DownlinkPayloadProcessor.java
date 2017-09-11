package com.hpe.iot.northbound.handler.outflow;

import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.DeviceModel;

public interface DownlinkPayloadProcessor {

	void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload);

}
