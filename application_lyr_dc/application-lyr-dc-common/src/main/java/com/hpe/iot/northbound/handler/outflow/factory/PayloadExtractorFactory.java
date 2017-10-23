/**
 * 
 */
package com.hpe.iot.northbound.handler.outflow.factory;

import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;

/**
 * @author sveera
 *
 */
public interface PayloadExtractorFactory {

	PayloadCipher getPayloadCipher(String manufacturer, String modelId, String version);

	DownlinkPayloadProcessor getDownlinkPayloadProcessor(String manufacturer, String modelId, String version);

}
