/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.holder;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.mqtt.southbound.service.outflow.SouthboundPublisherService;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.southbound.http.utility.HttpClientUtility;

/**
 * @author sveera
 *
 */
public class GroovyServicesHolder {

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;
	private final SouthboundPublisherService southboundPublisherService;
	private final HttpClientUtility httpClientUtility;

	public GroovyServicesHolder(IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService,
			SouthboundPublisherService southboundPublisherService, HttpClientUtility httpClientUtility) {
		super();
		this.iotPublisherService = iotPublisherService;
		this.southboundPublisherService = southboundPublisherService;
		this.httpClientUtility = httpClientUtility;
	}

	public IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> getIotPublisherService() {
		return iotPublisherService;
	}

	public SouthboundPublisherService getSouthboundPublisherService() {
		return southboundPublisherService;
	}

	public HttpClientUtility getHttpClientUtility() {
		return httpClientUtility;
	}

}
