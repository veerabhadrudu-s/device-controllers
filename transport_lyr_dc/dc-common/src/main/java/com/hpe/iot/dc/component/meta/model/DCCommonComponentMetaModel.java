package com.hpe.iot.dc.component.meta.model;

import java.util.ArrayList;
import java.util.List;

import com.hpe.iot.dc.service.MessageService;

/**
 * @author sveera
 *
 */
public class DCCommonComponentMetaModel {

	private List<Class<? extends MessageService>> messageServiceTypes;

	public DCCommonComponentMetaModel() {
		super();
		this.messageServiceTypes = new ArrayList<>();
	}

	public void addMessageServiceType(Class<? extends MessageService> messageServiceType) {
		messageServiceTypes.add(messageServiceType);
	}

	public List<Class<? extends MessageService>> getMessageServiceTypes() {
		return messageServiceTypes;
	}


}
