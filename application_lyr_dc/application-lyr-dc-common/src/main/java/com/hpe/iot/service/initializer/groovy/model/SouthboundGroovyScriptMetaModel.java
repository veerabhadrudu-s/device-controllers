/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.model;

import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;

/**
 * @author sveera
 */
public class SouthboundGroovyScriptMetaModel {
	private Class<? extends DeviceIdExtractor> deviceIdExtractorClasstype;
	private Class<? extends MessageTypeExtractor> messageTypeExtractorClasstype;
	private Class<? extends PayloadDecipher> payloadDecipherClasstype;
	private Class<? extends UplinkPayloadProcessor> uplinkPayloadProcessorClasstype;

	public Class<? extends DeviceIdExtractor> getDeviceIdExtractorClasstype() {
		return deviceIdExtractorClasstype;
	}

	public void setDeviceIdExtractorClasstype(Class<? extends DeviceIdExtractor> deviceIdExtractorClasstype) {
		this.deviceIdExtractorClasstype = deviceIdExtractorClasstype;
	}

	public Class<? extends MessageTypeExtractor> getMessageTypeExtractorClasstype() {
		return messageTypeExtractorClasstype;
	}

	public void setMessageTypeExtractorClasstype(Class<? extends MessageTypeExtractor> messageTypeExtractorClasstype) {
		this.messageTypeExtractorClasstype = messageTypeExtractorClasstype;
	}

	public Class<? extends PayloadDecipher> getPayloadDecipherClasstype() {
		return payloadDecipherClasstype;
	}

	public void setPayloadDecipherClasstype(Class<? extends PayloadDecipher> payloadDecipherClasstype) {
		this.payloadDecipherClasstype = payloadDecipherClasstype;
	}

	public Class<? extends UplinkPayloadProcessor> getUplinkPayloadProcessorClasstype() {
		return uplinkPayloadProcessorClasstype;
	}

	public void setUplinkPayloadProcessorClasstype(
			Class<? extends UplinkPayloadProcessor> uplinkPayloadProcessorClasstype) {
		this.uplinkPayloadProcessorClasstype = uplinkPayloadProcessorClasstype;
	}

	@Override
	public String toString() {
		return "SouthboundGroovyScriptMetaModel [deviceIdExtractorClasstype=" + deviceIdExtractorClasstype
				+ ", messageTypeExtractorClasstype=" + messageTypeExtractorClasstype + ", payloadDecipherClasstype="
				+ payloadDecipherClasstype + ", uplinkPayloadProcessorClasstype=" + uplinkPayloadProcessorClasstype
				+ "]";
	}

}