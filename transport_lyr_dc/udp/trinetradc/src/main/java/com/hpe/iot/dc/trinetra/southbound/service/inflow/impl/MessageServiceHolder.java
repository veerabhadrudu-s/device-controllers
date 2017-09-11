/**
 * 
 */
package com.hpe.iot.dc.trinetra.southbound.service.inflow.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.service.MessageService;

/**
 * @author sveera
 *
 */
@Component
public class MessageServiceHolder {

	private final List<MessageService> messageServices;

	@Autowired
	public MessageServiceHolder(List<MessageService> messageServices) {
		super();
		this.messageServices = messageServices;
	}

	public List<MessageService> getMessageServices() {
		return messageServices;
	}

}
