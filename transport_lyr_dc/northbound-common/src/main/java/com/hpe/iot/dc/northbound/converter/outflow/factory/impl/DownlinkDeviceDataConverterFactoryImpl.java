package com.hpe.iot.dc.northbound.converter.outflow.factory.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.converter.outflow.ExtendedDownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.converter.outflow.factory.DownlinkDeviceDataConverterFactory;

/**
 * @author sveera
 *
 */
public class DownlinkDeviceDataConverterFactoryImpl implements DownlinkDeviceDataConverterFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, DownlinkDeviceDataConverter> metaModelConverters = new HashMap<String, DownlinkDeviceDataConverter>();

	public DownlinkDeviceDataConverterFactoryImpl(final List<DownlinkDeviceDataConverter> converters) {
		for (DownlinkDeviceDataConverter metaModelConverter : converters) {
			handleForExtendedMetaModelConverter(metaModelConverter);
			metaModelConverters.put(metaModelConverter.getMessageType(), metaModelConverter);
		}
		logger.debug("Supported Message Converter Types are " + metaModelConverters);
	}

	private void handleForExtendedMetaModelConverter(DownlinkDeviceDataConverter metaModelConverter) {
		if (metaModelConverter instanceof ExtendedDownlinkDeviceDataConverter) {
			for (String supportedMessageType : ((ExtendedDownlinkDeviceDataConverter) metaModelConverter).getMessageTypes()) {
				metaModelConverters.put(supportedMessageType, metaModelConverter);
			}
		}
	}

	public DownlinkDeviceDataConverter getModelConverter(String messageType) {
		return metaModelConverters.get(messageType);
	}

}
