
package com.hpe.iot.dc.northbound.transformer.outflow.impl;

import static com.hpe.iot.dc.model.constants.ModelConstants.MESSAGE_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.converter.outflow.factory.DownlinkDeviceDataConverterFactory;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class DefaultDownlinkDataModelTransformerImpl implements DownlinkDataModelTransformer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DownlinkDeviceDataConverterFactory downlinkDeviceDataConverterFactory;
	public DefaultDownlinkDataModelTransformerImpl(DownlinkDeviceDataConverterFactory metaModelFactory) {
		super();
		this.downlinkDeviceDataConverterFactory = metaModelFactory;
	}

	@Override
	public List<DeviceInfo> convertToModel(DeviceModel deviceModel, JsonObject downlinkData) {
		List<DeviceInfo> convertedDownlinkData = new ArrayList<>();
		String messageType = downlinkData.get(MESSAGE_TYPE).getAsString();
		DownlinkDeviceDataConverter downlinkDeviceDataConverter = downlinkDeviceDataConverterFactory
				.getModelConverter(messageType);
		if (downlinkDeviceDataConverter != null) {
			convertedDownlinkData.add(downlinkDeviceDataConverter.createModel(deviceModel, downlinkData));
		} else {
			logger.warn("DownlinkDeviceDataConverter not available for message Type " + messageType
					+ " for device Model" + deviceModel);
		}
		return convertedDownlinkData;
	}

	@Override
	public String toString() {
		return "DefaultDownlinkDataModelTransformerImpl [downlinkDeviceDataConverterFactory="
				+ downlinkDeviceDataConverterFactory + "]";
	}

}
