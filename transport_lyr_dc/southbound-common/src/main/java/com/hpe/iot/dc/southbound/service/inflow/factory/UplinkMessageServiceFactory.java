package com.hpe.iot.dc.southbound.service.inflow.factory;

import com.hpe.iot.dc.service.factory.MessageServiceFactory;
import com.hpe.iot.dc.southbound.service.inflow.UplinkMessageService;

/**
 * @author sveera
 *
 */
public interface UplinkMessageServiceFactory extends MessageServiceFactory {

	UplinkMessageService getUpLinkMessageService(String messageType);

}
