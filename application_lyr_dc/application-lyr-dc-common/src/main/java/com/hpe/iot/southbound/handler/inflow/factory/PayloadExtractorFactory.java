/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.factory;

import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;

/**
 * @author sveera
 *
 */
public interface PayloadExtractorFactory {

	DeviceIdExtractor getDeviceIdExtractor(String manufacturer, String modelId, String version);

	MessageTypeExtractor getMessageTypeExtractor(String manufacturer, String modelId, String version);

	PayloadDecipher getPayloadDecipher(String manufacturer, String modelId, String version);

	UplinkPayloadProcessor getUplinkPayloadProcessor(String manufacturer, String modelId, String version);

}
