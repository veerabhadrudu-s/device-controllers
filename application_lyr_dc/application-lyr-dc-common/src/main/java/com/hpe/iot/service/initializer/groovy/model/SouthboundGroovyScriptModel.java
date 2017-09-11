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
public class SouthboundGroovyScriptModel {
	private DeviceIdExtractor deviceIdExtractor;
	private MessageTypeExtractor messageTypeExtractor;
	private PayloadDecipher payloadDecipher;
	private UplinkPayloadProcessor uplinkPayloadProcessor;

	public DeviceIdExtractor getDeviceIdExtractor() {
		return deviceIdExtractor;
	}

	public void setDeviceIdExtractor(DeviceIdExtractor deviceIdExtractor) {
		this.deviceIdExtractor = deviceIdExtractor;
	}

	public MessageTypeExtractor getMessageTypeExtractor() {
		return messageTypeExtractor;
	}

	public void setMessageTypeExtractor(MessageTypeExtractor messageTypeExtractor) {
		this.messageTypeExtractor = messageTypeExtractor;
	}

	public PayloadDecipher getPayloadDecipher() {
		return payloadDecipher;
	}

	public void setPayloadDecipher(PayloadDecipher payloadDecipher) {
		this.payloadDecipher = payloadDecipher;
	}

	public UplinkPayloadProcessor getUplinkPayloadProcessor() {
		return uplinkPayloadProcessor;
	}

	public void setUplinkPayloadProcessor(UplinkPayloadProcessor uplinkPayloadProcessor) {
		this.uplinkPayloadProcessor = uplinkPayloadProcessor;
	}

	@Override
	public String toString() {
		return "SouthboundGroovyScriptModel [deviceIdExtractor=" + deviceIdExtractor + ", messageTypeExtractor="
				+ messageTypeExtractor + ", payloadDecipher=" + payloadDecipher + ", uplinkPayloadProcessor="
				+ uplinkPayloadProcessor + "]";
	}

}