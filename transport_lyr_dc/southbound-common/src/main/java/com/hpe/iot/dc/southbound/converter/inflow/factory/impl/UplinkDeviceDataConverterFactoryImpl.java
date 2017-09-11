package com.hpe.iot.dc.southbound.converter.inflow.factory.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.southbound.converter.inflow.ExtendedUplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;

/**
 * @author sveera
 *
 */
public class UplinkDeviceDataConverterFactoryImpl implements UplinkDeviceDataConverterFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, UplinkDeviceDataConverter> metaModelConverters = new HashMap<String, UplinkDeviceDataConverter>();

	public UplinkDeviceDataConverterFactoryImpl(final List<UplinkDeviceDataConverter> converters) {
		for (UplinkDeviceDataConverter metaModelConverter : converters) {
			handleForExtendedMetaModelConverter(metaModelConverter);
			metaModelConverters.put(metaModelConverter.getMessageType(), metaModelConverter);
		}
		logger.debug("Supported Message Converter Types are " + metaModelConverters);
	}

	private void handleForExtendedMetaModelConverter(UplinkDeviceDataConverter metaModelConverter) {
		if (metaModelConverter instanceof ExtendedUplinkDeviceDataConverter) {
			for (String supportedMessageType : ((ExtendedUplinkDeviceDataConverter) metaModelConverter).getMessageTypes()) {
				metaModelConverters.put(supportedMessageType, metaModelConverter);
			}
		}
	}

	public UplinkDeviceDataConverter getModelConverter(String messageType) {
		return metaModelConverters.get(messageType);
	}

}
