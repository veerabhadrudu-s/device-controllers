/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.model;

import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;

/**
 * @author sveera
 *
 */
public class NorthboundGroovyScriptModel {

	private PayloadCipher payloadCipher;
	private DownlinkPayloadProcessor downlinkPayloadProcessor;

	public PayloadCipher getPayloadCipher() {
		return payloadCipher;
	}

	public void setPayloadCipher(PayloadCipher payloadCipher) {
		this.payloadCipher = payloadCipher;
	}

	public DownlinkPayloadProcessor getDownlinkPayloadProcessor() {
		return downlinkPayloadProcessor;
	}

	public void setDownlinkPayloadProcessor(DownlinkPayloadProcessor downlinkPayloadProcessor) {
		this.downlinkPayloadProcessor = downlinkPayloadProcessor;
	}

	@Override
	public String toString() {
		return "NorthboundGroovyScriptModel [payloadCipher=" + payloadCipher + ", downlinkPayloadProcessor="
				+ downlinkPayloadProcessor + "]";
	}

}
