package com.hpe.iot.http.groovyscript.sample.model;

import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.model.DeviceInfo
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor
import com.hpe.iot.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.southbound.handler.inflow.impl.AbstractJsonPathDeviceIdExtractor
import com.hpe.iot.southbound.handler.inflow.impl.DefaultGroovyMessageTypeExtractor
import com.hpe.iot.southbound.handler.inflow.impl.DefaultPayloadDecipher
import com.hpe.iot.southbound.handler.inflow.impl.DefaultUplinkPayloadProcessor



public class SampleDeviceModel implements DeviceModel{

	@Override
	public String getManufacturer(){
		return "Sample"
	}

	@Override
	public String getModelId(){
		return "Model"
	}
	@Override
	public String getVersion(){
		return "1.0"
	}
}

public class SampleModelDeviceIdExtractor extends AbstractJsonPathDeviceIdExtractor{
	public String getDeviceIdJsonPath(){
		return "\$.deviceId";
	}
}

public class SampleModelMessageTypeExtractor extends DefaultGroovyMessageTypeExtractor{
}

public class SampleModelPayloadDecipher extends DefaultPayloadDecipher{
}

public class SampleModelPayloadProcessor extends DefaultUplinkPayloadProcessor{

	public SampleModelPayloadProcessor(IOTPublisherService iotPublisherService){
		super(iotPublisherService);
	}
}


public class SampleDownlinkPayloadProcessor implements DownlinkPayloadProcessor{

	@Override
	public void processPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload) {
	}
}
