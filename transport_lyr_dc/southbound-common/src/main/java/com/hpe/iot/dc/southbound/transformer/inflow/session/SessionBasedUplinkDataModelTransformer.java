/**
 * 
 */
package com.hpe.iot.dc.southbound.transformer.inflow.session;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.util.DataParserUtility;
import com.hpe.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public abstract class SessionBasedUplinkDataModelTransformer implements UplinkDataModelTransformer {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory;

	public SessionBasedUplinkDataModelTransformer(UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory) {
		super();
		this.uplinkDeviceDataConverterFactory = uplinkDeviceDataConverterFactory;
	}

	@Override
	public List<DeviceInfo> convertToModel(DeviceModel deviceModel, byte[] input) {
		if (deviceModel==null || !(deviceModel instanceof Device))
			return Collections.<DeviceInfo> emptyList();
		byte[] messageBytes = DataParserUtility.truncateEmptyBytes(input);
		UtilityLogger.logRawDataInDecimalFormat(messageBytes, getClass());
		UtilityLogger.logRawDataInHexaDecimalFormat(messageBytes, getClass());
		return convertToModelForDevice((Device) deviceModel, messageBytes);
	}

	protected abstract List<DeviceInfo> convertToModelForDevice(Device device, byte[] input);

}
