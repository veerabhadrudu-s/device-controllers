/**
 * 
 */
package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;

/**
 * @author sveera
 *
 */
@Component
public class UplinkDeviceDataConverterHolder {

	private final List<UplinkDeviceDataConverter> uplinkDeviceDataConverters;

	@Autowired
	public UplinkDeviceDataConverterHolder(List<UplinkDeviceDataConverter> uplinkDeviceDataConverters) {
		super();
		this.uplinkDeviceDataConverters = uplinkDeviceDataConverters;
	}

	public List<UplinkDeviceDataConverter> getUplinkDeviceDataConverters() {
		return uplinkDeviceDataConverters;
	}

}
