package com.hpe.iot.dc.service.factory;

import com.hpe.iot.dc.service.MessageService;

/**
 * @author sveera
 *
 */
public interface MessageServiceFactory {

	MessageService getMessageService(String messageType);

}
