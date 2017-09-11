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
public class NorthboundGroovyScriptMetaModel {

	private Class<? extends PayloadCipher> payloadCipherClasstype;
	private Class<? extends DownlinkPayloadProcessor> downlinkPayloadProcessorClasstype;

	public Class<? extends PayloadCipher> getPayloadCipherClasstype() {
		return payloadCipherClasstype;
	}

	public void setPayloadCipherClasstype(Class<? extends PayloadCipher> payloadCipherClasstype) {
		this.payloadCipherClasstype = payloadCipherClasstype;
	}

	public Class<? extends DownlinkPayloadProcessor> getDownlinkPayloadProcessorClasstype() {
		return downlinkPayloadProcessorClasstype;
	}

	public void setDownlinkPayloadProcessorClasstype(
			Class<? extends DownlinkPayloadProcessor> downlinkPayloadProcessorClasstype) {
		this.downlinkPayloadProcessorClasstype = downlinkPayloadProcessorClasstype;
	}

	@Override
	public String toString() {
		return "NorthboundGroovyScriptMetaModel [payloadCipherClasstype=" + payloadCipherClasstype
				+ ", downlinkPayloadProcessorClasstype=" + downlinkPayloadProcessorClasstype + "]";
	}

}
