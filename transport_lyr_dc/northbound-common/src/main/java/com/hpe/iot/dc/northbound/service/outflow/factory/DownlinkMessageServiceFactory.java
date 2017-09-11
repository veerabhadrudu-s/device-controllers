package com.hpe.iot.dc.northbound.service.outflow.factory;

import com.hpe.iot.dc.northbound.service.outflow.DownlinkMessageService;
import com.hpe.iot.dc.service.factory.MessageServiceFactory;

/**
 * @author sveera
 *
 */
public interface DownlinkMessageServiceFactory extends MessageServiceFactory {

	DownlinkMessageService getDownlinkMessageService(String messageType);

}
