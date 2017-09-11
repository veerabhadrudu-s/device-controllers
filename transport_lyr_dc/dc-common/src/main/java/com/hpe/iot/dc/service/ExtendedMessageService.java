package com.hpe.iot.dc.service;

import java.util.List;

/**
 * @author sveera
 *
 */
public interface ExtendedMessageService extends MessageService {

	List<String> getMessageTypes();
}
