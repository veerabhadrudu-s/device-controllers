/**
 * 
 */
package com.hpe.iot.http.southbound.service.holder;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.southbound.http.utility.HttpClientUtility;

/**
 * @author sveera
 *
 */
public class GroovyServicesHolder {

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;
	private final HttpClientUtility httpClientUtility;

	public GroovyServicesHolder(IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService,
			HttpClientUtility httpClientUtility) {
		super();
		this.iotPublisherService = iotPublisherService;
		this.httpClientUtility = httpClientUtility;
	}

	public IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> getIotPublisherService() {
		return iotPublisherService;
	}

	public HttpClientUtility getHttpClientUtility() {
		return httpClientUtility;
	}

}
