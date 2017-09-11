package com.hpe.iot.dc.service;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;

/**
 * @author sveera
 *
 */
public interface MessageService {

	String getMessageType();

	DeviceDataDeliveryStatus executeService(DeviceInfo deviceInfo);

}
