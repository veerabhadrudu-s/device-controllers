package com.hpe.iot.dc.tcp.southbound.model;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface ServerSocketToDeviceModel extends DeviceModel {

	String getDescription();

	String getBoundLocalAddress();

	int getPortNumber();

	TCPOptions getTCPOptions();

}
