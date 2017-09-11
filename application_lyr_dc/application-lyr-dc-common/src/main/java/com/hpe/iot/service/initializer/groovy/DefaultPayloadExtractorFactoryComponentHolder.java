/**
 * 
 */
package com.hpe.iot.service.initializer.groovy;

import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.impl.DefaultPayloadCipher;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultGroovyMessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultPayloadDecipher;

/**
 * @author sveera
 *
 */
public class DefaultPayloadExtractorFactoryComponentHolder {

	private final MessageTypeExtractor defaultMessageTypeExtractor;
	private final PayloadDecipher defaultPayloadDecipher;
	private final UplinkPayloadProcessor defaultUplinkPayloadProcessor;
	private final PayloadCipher defaultpayloadCipher;

	public DefaultPayloadExtractorFactoryComponentHolder(UplinkPayloadProcessor defaultUplinkPayloadProcessor) {
		super();
		this.defaultUplinkPayloadProcessor = defaultUplinkPayloadProcessor;
		this.defaultMessageTypeExtractor = new DefaultGroovyMessageTypeExtractor();
		this.defaultPayloadDecipher = new DefaultPayloadDecipher();
		this.defaultpayloadCipher = new DefaultPayloadCipher();
	}

	public MessageTypeExtractor getDefaultMessageTypeExtractor() {
		return defaultMessageTypeExtractor;
	}

	public PayloadDecipher getDefaultPayloadDecipher() {
		return defaultPayloadDecipher;
	}

	public UplinkPayloadProcessor getDefaultUplinkPayloadProcessor() {
		return defaultUplinkPayloadProcessor;
	}

	public PayloadCipher getDefaultpayloadCipher() {
		return defaultpayloadCipher;
	}

}
